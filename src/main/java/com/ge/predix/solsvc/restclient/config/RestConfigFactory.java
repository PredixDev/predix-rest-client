/*
 * Copyright (c) 2018 General Electric Company. All rights reserved.
 *
 * The copyright to the computer software herein is the property of
 * General Electric Company. The software may be used and/or copied only
 * with the written permission of General Electric Company or in accordance
 * with the terms and conditions stipulated in the agreement/contract
 * under which the software has been supplied.
 */

package com.ge.predix.solsvc.restclient.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * If you are not using Environment variables (or properties), you can
 * programatically create a Config using this Factory class. This will use the
 * DefaultConfig as a baseline. This is helpful when your microservice is
 * handling multiple user or clients or endpoints
 * 
 * @author predix -
 */
@Component
public class RestConfigFactory implements ApplicationContextAware {

	@Autowired
	private static ApplicationContext context;

	/**
	 * Use this to generate a client token with grant_type=client_credentials
	 * 
	 * @param oauthIssuerId
	 *            - Issuer ID URL of the UAA
	 * @param oauthClientIdColonSecret
	 *            - clientId:secret separated by a colon
	 * @param oauthClientIdEncode
	 *            - whether the SDK should base64 encode the clientId:secret,
	 *            default is true. true means it is clear-text. false is what
	 *            you should do in order to not display the secret in
	 *            clear-text.
	 * @return -
	 */
	@SuppressWarnings("nls")
	static public IOauthRestConfig clientCredentials(String oauthIssuerId, String oauthClientIdColonSecret,
			boolean oauthClientIdEncode) {
		DefaultOauthRestConfig defaultConfig = (DefaultOauthRestConfig) context.getBean("defaultOauthRestConfig");
		setClientCredentialProps(oauthIssuerId, oauthClientIdColonSecret, oauthClientIdEncode, defaultConfig);
		return defaultConfig;
	}

	/**
	 * Use this to generate a client token with grant_type=password
	 * 
	 * @param oauthIssuerId
	 *            - Issuer ID URL of the UAA
	 * @param oauthClientIdColonSecret
	 *            - clientId:secret separated by a colon
	 * @param oauthClientIdEncode
	 *            - whether the clientId:secret is base 64 encoded when passed
	 *            in, default is true
	 * @param oauthUserName
	 *            - the username to generate a token for. As set up in UAA or
	 *            the UAA Federated IDP.
	 * @param oauthUserPassword
	 *            - the password of the user
	 * @param oauthEncodeUserPassword
	 *            - whether to base64 encode the password, default is true
	 * @return -
	 */
	static public IOauthRestConfig passwordGrant(String oauthIssuerId, String oauthClientIdColonSecret,
			boolean oauthClientIdEncode, String oauthUserName, String oauthUserPassword,
			boolean oauthEncodeUserPassword) {
		DefaultOauthRestConfig defaultConfig = context.getBean(DefaultOauthRestConfig.class);
		defaultConfig.setGrantType("password"); //$NON-NLS-1$
		setPasswordProps(oauthIssuerId, oauthClientIdColonSecret, oauthClientIdEncode, oauthUserName, oauthUserPassword,
				oauthEncodeUserPassword, defaultConfig);
		return defaultConfig;
	}

	/**
	 * @param oauthIssuerId
	 *            -
	 * @param oauthClientId
	 *            -
	 * @param oauthClientIdEncode
	 *            -
	 * @param defaultConfig
	 *            -
	 */
	public static void setClientCredentialProps(String oauthIssuerId, String oauthClientId, boolean oauthClientIdEncode,
			DefaultOauthRestConfig defaultConfig) {
		defaultConfig.setOauthIssuerId(oauthIssuerId);
		defaultConfig.setOauthClientId(oauthClientId);
		defaultConfig.setOauthClientIdEncode(oauthClientIdEncode);
	}

	/**
	 * @param oauthIssuerId
	 *            -
	 * @param oauthClientId
	 *            -
	 * @param oauthClientIdEncode
	 *            -
	 * @param oauthUserName
	 *            -
	 * @param oauthUserPassword
	 *            -
	 * @param oauthEncodeUserPassword
	 *            -
	 * @param defaultConfig
	 *            -
	 */
	public static void setPasswordProps(String oauthIssuerId, String oauthClientId, boolean oauthClientIdEncode,
			String oauthUserName, String oauthUserPassword, boolean oauthEncodeUserPassword,
			DefaultOauthRestConfig defaultConfig) {
		setClientCredentialProps(oauthIssuerId, oauthClientId, oauthClientIdEncode, defaultConfig);
		defaultConfig.setOauthUserName(oauthUserName);
		defaultConfig.setOauthUserPassword(oauthUserPassword);
		defaultConfig.setOauthEncodeUserPassword(oauthEncodeUserPassword);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.context.ApplicationContextAware#setApplicationContext
	 * (org.springframework.context.ApplicationContext)
	 */
	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		RestConfigFactory.context = context;
	}

}
