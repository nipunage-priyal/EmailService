package com.service;

import java.util.Date;
import java.util.function.Function;
import java.util.function.Supplier;

import com.config.ProjectConfig;
import com.model.Recipient;

public class MessageBuilder {
	static Function<String, String> dynamicMessageBuilder = (recepientName) -> {
		return "Hello " + recepientName == null ? "" : recepientName;
	};
	static Supplier<String> genericMessageBuilder = () -> {
		return String.join("\n", FileService.fileReader.apply(ProjectConfig.MESSAGE_TEXT_FILE));
	};
	Function<Recipient, String> auditMessageBuilder = (recipient) -> {
		return recipient.getName() + "\t" + recipient.getEmailId() + "\t" + recipient.getCompany() + "\t" + new Date();
	};
	private String genericMessage;

	public String getMessage(final Recipient rec) {
		return dynamicMessageBuilder.apply(rec.getName()) + "\n" + getGenericMessage();
	}

	private String getGenericMessage() {
		if (genericMessage == null)
			genericMessage = genericMessageBuilder.get();
		return genericMessage;
	}
}
