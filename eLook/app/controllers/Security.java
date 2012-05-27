package controllers;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;

import play.cache.Cache;
import play.libs.Codec;
import play.libs.Images;
import models.User;

public class Security extends Secure.Security {
    
    static boolean authenticate(String username, String password) {
        User user = User.find("name", username).first();
        return user != null && user.password.equals(Codec.hexMD5(password));
    }
    
    static boolean check(String name) {
        String username = session.get("username");
    	if (name.equals(username)) {
            return true;
        }
        else {
            return false;
        }
    }    
    
    
    public static void captcha() {
        Images.Captcha captcha = Images.captcha();
        Cache.set(session.getId() + "vcode", captcha.getText().toLowerCase());
        flash.keep("url");
        renderBinary(captcha);
    }
    
}