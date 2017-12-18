package com.service;

import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.config.ProjectConfig;
import com.model.Recipient;

public class FilterSuppliers {
	Set<String> companiesToAvoid = null;
	Set<String> emailsToAvoid = null;

	private final Supplier<Set<String>> avoidCompaniesSupplier = () -> {
		return FileService.fileReader.apply(ProjectConfig.AVOID_COMPANIES_FILE).stream().collect(Collectors.toSet());
	};
	private final Supplier<Set<String>> sentEmailsSupplier = () -> {
		return FileService.fileReader.apply(ProjectConfig.SENT_EMAILS_FILE).stream().collect(Collectors.toSet());
	};

	public boolean shouldSendtoCompany(final Recipient rec) {
		companiesToAvoid = companiesToAvoid == null ? avoidCompaniesSupplier.get() : companiesToAvoid;
		return companiesToAvoid != null && !companiesToAvoid.contains(rec.getCompany());
	}

	public boolean shouldSendtoEmail(final Recipient rec) {
		emailsToAvoid = emailsToAvoid == null ? sentEmailsSupplier.get() : emailsToAvoid;
		return emailsToAvoid != null && !emailsToAvoid.contains(rec.getEmailId());
	}

	public boolean shouldSend(final Recipient rec) {
		return shouldSendtoCompany(rec) || shouldSendtoEmail(rec);
	}

}
