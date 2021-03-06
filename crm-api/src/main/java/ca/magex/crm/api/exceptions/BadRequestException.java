package ca.magex.crm.api.exceptions;

import java.util.Arrays;
import java.util.List;

import ca.magex.crm.api.services.Crm;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Message;

public class BadRequestException extends ApiException {

	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;

	public BadRequestException(List<Message> messages) {
		super("Bad Request: " + messages);
	}
	
	public BadRequestException(Identifier base, String type, String path, String message) {
		this(Arrays.asList(new Message(base, type, path, message)));
	}

	@Override
	public int getErrorCode() {
		return 400;
	}
	
}
