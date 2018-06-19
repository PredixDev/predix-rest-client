package com.ge.predix.solsvc.restclient.testconfig;

import org.springframework.stereotype.Component;

import com.ge.predix.solsvc.restclient.config.DefaultOauthRestConfig;
import com.ge.predix.solsvc.restclient.config.IOauthRestConfig;

/**
 * 
 * @author predix
 */
@Component("testOauthRestConfig1")
@SuppressWarnings({})
public class TestOauthRestConfig1 extends DefaultOauthRestConfig implements IOauthRestConfig {
	//
}
