package gov.ca.modeling.timeseries.map.server.data.persistence;

import gov.ca.modeling.timeseries.map.shared.data.TimeSeriesData;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;

public class TestTimeSeriesDAO extends BaseDatastoreTestCase {

	public TimeSeriesData saveTimeSeries(TimeSeriesDataDAO dao, double[] values,
			int checkNumberAfterAdd) {
		TimeSeriesData ts1 = new TimeSeriesData();
		ts1.setInterval("15MIN");
		ts1.setStartTime("01JAN1980 0100");
		ts1.setValues(values);
		return dao.createObject(ts1);
	}

	protected void checkNumber(int number) {
		Objectify ofy = ObjectifyService.begin();
		assertEquals(number, ofy.query(TimeSeriesData.class).countAll());
	}

	public void testCreate() {
		TimeSeriesDataDAO dao = new TimeSeriesDataDAOImpl();
		double[] values = new double[] { 1, 3, 5, 7, 9, 11 };
		saveTimeSeries(dao, values, 1);
		checkNumber(1);
		double[] values2 = new double[] { 1, 3, 5, 7, 9, 11 };
		saveTimeSeries(dao, values2, 2);
		checkNumber(2);
	}

	public void testDelete() {
		TimeSeriesDataDAO dao = new TimeSeriesDataDAOImpl();
		double[] values = new double[] { 1, 3, 5, 7, 9, 11 };
		TimeSeriesData user1 = saveTimeSeries(dao, values, 1);
		checkNumber(1);
		dao.deleteObject(user1);
		checkNumber(0);
	}

	public void testRetrieveById() {
		TimeSeriesDataDAO dao = new TimeSeriesDataDAOImpl();
		double[] values = new double[] { 1, 3, 5, 7, 9, 11 };
		TimeSeriesData user1 = saveTimeSeries(dao, values, 1);
		TimeSeriesData user2 = dao.findObjectById(TimeSeriesData.class, user1.getId());
		assertEquals(user1.getId(), user2.getId());
		TimeSeriesData user3 = dao.findObjectById(TimeSeriesData.class,
				user1.getId() + 100);
		assertNull(user3);
	}

	public void testLargeSet() {
		double[] values = new double[127000];
		TimeSeriesDataDAO dao = new TimeSeriesDataDAOImpl();
		TimeSeriesData user1 = saveTimeSeries(dao, values, 1);
		assertFalse(new Long(0).equals(user1.getId()));
	}
}
