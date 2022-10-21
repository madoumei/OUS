package com.event.listener.passListen;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.event.event.PassEvent;
/**
 * 下发ic卡
 *
 */
@Component
public class IcCardPassListen {

	
	 @EventListener(value = PassEvent.class,condition = "#passEvent.eqType.contains('3')")
	 public void listener(PassEvent passEvent) {

    }
}
