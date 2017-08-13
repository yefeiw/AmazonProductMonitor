package com.yefeiw.service.impl;

import com.yefeiw.domain.*;
import com.yefeiw.service.UserInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Service
public class UserInfoServiceImpl implements UserInfoService {

    @Autowired
    private UserInformationRepository userInformationRepository;

    @Autowired
    private AdRepository adRepository;

    private Logger logger = LoggerFactory.getLogger(UserInfoServiceImpl.class);
    @Override
    public List<UserInformation> saveUserInfo(List<UserInformation> userInfo) {
        return userInformationRepository.save(userInfo);

    }
    @Override
    public List<UserInformation> findByUsername(String username) {
        List<UserInformation> ret = userInformationRepository.findByUsername(username);
        for (UserInformation information : ret) {
            List<Subscription> subscriptions = information.getSubscriptionList();
            logger.info("Getting subscription for user " + information.getUsername());
            List<Ad> emailList = new ArrayList<>();
            for (Subscription cand : subscriptions) {
                List<Ad> topKAds = adRepository.findAllByCategoryOrderByDiscount(cand.getCategory());
                logger.info("Found " + topKAds.size() + " ads for category " + cand.getCategory() + " for user " + information.getUsername());
                emailList.addAll(topKAds);
            }
            //Send email
            try {
                generateAndSendEmail(information, emailList);
            } catch (Exception e) {
                logger.error("Error sending email");
                e.printStackTrace();
            }
        }



        return ret;
    }

    @Override
    public List<UserInformation> findByEmail(String email) {
        return userInformationRepository.findByEmail(email);
    }


     private void generateAndSendEmail(UserInformation recipient, List<Ad> body) throws AddressException, MessagingException {

        // Step1
        Properties mailServerProperties = System.getProperties();
        mailServerProperties.put("mail.smtp.port", "587");
        mailServerProperties.put("mail.smtp.auth", "true");
        mailServerProperties.put("mail.smtp.starttls.enable", "true");

        // Step2
        Session getMailSession = Session.getDefaultInstance(mailServerProperties, null);
        MimeMessage  generateMailMessage = new MimeMessage(getMailSession);
        generateMailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient.getEmail()));
        generateMailMessage.setSubject("Discounts!");
        String emailBody = "Hi Dear " +recipient.getUsername()+
                "<br><br>Here are the latest updates of your favorite Discounts</br>";
        for(Ad ad : body) {
            emailBody += "<br>"+ad.title+"</br><br>"+ad.discount+"</br><br><img src=\""+ad.thumbnail+"\"/></br>";
        }
        emailBody += "<br> Regards, <br>Yefei Wang";
        generateMailMessage.setContent(emailBody, "text/html");

        // Step3
        System.out.println("\n\n Get Session and Send mail");
        Transport transport = getMailSession.getTransport("smtp");

        // Enter your correct gmail UserID and Password
        // if you have 2FA enabled then provide App Specific Password
        transport.connect("smtp.gmail.com", "jeffxanthus@gmail.com", "AT900-Technique");
        transport.sendMessage(generateMailMessage, generateMailMessage.getAllRecipients());
        System.out.println("Email sent successfully");
        transport.close();
    }


}
