package com;

import com.service.SenderService;

public class MainClass {
	/**
	 * @param args @throws
	 */

	public static void main(final String[] args) {
		final SenderService ss = new SenderService();
		ss.sendEmail();
	}

}
