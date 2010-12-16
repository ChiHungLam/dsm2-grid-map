package gov.ca.bdo.modeling.dsm2.map.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface DSM2StudyEventHandler extends EventHandler {
	/**
	 * when study is declared to have changed
	 * @param event
	 */
	void onChange(DSM2StudyEvent event);
	/**
	 * when the study is renamed
	 * @param event
	 */
	void onStudyNameChange(DSM2StudyEvent event);
}
