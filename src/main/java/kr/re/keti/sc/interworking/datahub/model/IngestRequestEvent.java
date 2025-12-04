package kr.re.keti.sc.interworking.datahub.model;

import org.springframework.context.ApplicationEvent;

public class IngestRequestEvent extends ApplicationEvent {

	private static final long serialVersionUID = 7836460269922402596L;

	public IngestRequestEvent(Object source) {
		super(source);
		// TODO Auto-generated constructor stub
	}
}
