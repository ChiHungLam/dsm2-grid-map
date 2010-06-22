package gov.ca.modeling.timeseries.map.client.event;

import gov.ca.modeling.timeseries.map.shared.data.TimeSeriesReferenceData;

import com.google.gwt.event.shared.GwtEvent;

public class DataChangedEvent extends GwtEvent<DataChangedEventHandler> {
	public static Type<DataChangedEventHandler> TYPE = new Type<DataChangedEventHandler>();
	private TimeSeriesReferenceData reference;

	@Override
	public Type<DataChangedEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(DataChangedEventHandler handler) {
		handler.onDataChange(this);
	}

	public DataChangedEvent(TimeSeriesReferenceData reference) {
		this.setReference(reference);
	}

	public void setReference(TimeSeriesReferenceData reference) {
		this.reference = reference;
	}

	public TimeSeriesReferenceData getReference() {
		return reference;
	}

}
