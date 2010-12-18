/*******************************************************************************
 *     Copyright (C) 2009, 2010 Nicky Sandhu, State of California, Department of Water Resources.
 *
 *     DSM2 Grid Map : An online map centric tool to visualize, create and modify 
 *                               DSM2 input and output 
 *     Version 1.0
 *     by Nicky Sandhu
 *     California Dept. of Water Resources
 *     Modeling Support Branch
 *     1416 Ninth Street
 *     Sacramento, CA 95814
 *     psandhu@water.ca.gov
 *
 *     Send bug reports to psandhu@water.ca.gov
 *
 *     This file is part of DSM2 Grid Map
 *     The DSM2 Grid Map is free software and is licensed to you under the terms of the GNU 
 *     General Public License, version 3, as published by the Free Software Foundation.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program; if not, contact the 
 *     Free Software Foundation, 675 Mass Ave, Cambridge, MA
 *     02139, USA.
 *
 *     THIS SOFTWARE AND DOCUMENTATION ARE PROVIDED BY THE CALIFORNIA
 *     DEPARTMENT OF WATER RESOURCES AND CONTRIBUTORS "AS IS" AND ANY
 *     EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 *     IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 *     PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE CALIFORNIA
 *     DEPARTMENT OF WATER RESOURCES OR ITS CONTRIBUTORS BE LIABLE FOR
 *     ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *     CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT
 *     OR SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA OR PROFITS; OR
 *     BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 *     LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *     (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 *     USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 *     DAMAGE.
 *******************************************************************************/
package gov.ca.bdo.modeling.dsm2.map.client.presenter;

import gov.ca.bdo.modeling.dsm2.map.client.service.DSM2InputServiceAsync;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.ToggleButton;

public class DSM2GridMapPresenter extends DSM2ModelBasePresenter {
	public interface Display extends DSM2ModelBasePresenter.Display {
		public boolean isInEditMode();

		public void setEditMode(boolean editMode);

		public void turnOffTextAnnotation();

		public HasClickHandlers getAddButton();

		public HasClickHandlers getDeleteButton();

		public void startShowingBathymetryPoints();

		public void stopShowingBathymetryPoints();

		public void setAddingMode(boolean down);

		public void setDeletingMode(boolean down);
	}

	/*
	 * flag to remember state of UI initialization, is true if bind() has
	 * already been called.
	 */
	private boolean bound;

	public DSM2GridMapPresenter(DSM2InputServiceAsync dsm2InputService,
			SimpleEventBus eventBus, Display display,
			ContainerPresenter containerPresenter) {
		super(dsm2InputService, eventBus, display, containerPresenter);
		bound = false;
	}

	public void go(HasWidgets container) {
		super.go(container);
	}

	protected void bind() {
		if (bound) {
			return;
		}
		super.bind();
		final Display d = (Display) display;

		d.getAddButton().addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				boolean down = ((ToggleButton) event.getSource()).isDown();
				d.setAddingMode(down);
			}
		});

		d.getDeleteButton().addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				boolean down = ((ToggleButton) event.getSource()).isDown();
				d.setDeletingMode(down);
			}
		});

		bound = true;
	}
}
