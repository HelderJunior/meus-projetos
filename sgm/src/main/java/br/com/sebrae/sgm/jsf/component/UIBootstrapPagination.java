package br.com.sebrae.sgm.jsf.component;

import java.util.ArrayList;
import java.util.Collection;

import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.component.UINamingContainer;
import javax.faces.model.DataModel;

import br.com.sebrae.sgm.jsf.component.datamodel.PagedListDataModel;

@FacesComponent(value = "UIBootstrapPagination")
public class UIBootstrapPagination extends UINamingContainer {

	private DataModel<? extends Object> dataModel;
	private String dataTable;
	
	private Collection<PaginationPage> paginationPages;
	
	/**
	 * FIXME Consertar as exceptions!!!
	 */
	public void init() {

		UIComponent component = this.findComponent(dataTable);
		if (component == null) {
			throw new RuntimeException("Not fount component jsf '" + dataTable + "'!");
		} else if (!(component instanceof UIData)) {
			throw new RuntimeException("The type of compoment '" + dataTable + "' must be '" + UIData.class.getName() + "'!");
		} else if (!(((UIData)component).getValue() instanceof DataModel<?>)) {
			throw new RuntimeException("The type of compoment '" + dataTable + "' must contain a instance of " + DataModel.class.getName() + " in attribute value!");
		} else if (((UIData)component).getValue() instanceof PagedListDataModel<?>) {
			dataModel = (PagedListDataModel<?>) ((UIData)component).getValue();
			setPaginationPages(createNavigationsPages());
		} else {
			throw new RuntimeException("The type of compoment '" + dataTable + "' contain a value unexpected of '" + UIData.class.getName() + "'!");
		}
		
	}

	private Collection<PaginationPage> createNavigationsPages() {
		Collection<PaginationPage>  navigationPages = new ArrayList<PaginationPage>();
		Integer pageActive = ((PagedListDataModel<?>)  dataModel).getCurrentPage();
		Long totalPages = ((PagedListDataModel<?>) dataModel).getTotalPage();
		for (int i = 1; i <= totalPages; i++) {
			navigationPages.add(new PaginationPage(i, pageActive.equals(i), totalPages));
		}
		return navigationPages;
	}

	public void alterCurrentPage(PaginationPage navigationPage) {
		((PagedListDataModel<?>) dataModel).alterCurrentPage(navigationPage.getNumberPage());
	}
	
	public void back(PaginationPage navigationPage) {
		((PagedListDataModel<?>) dataModel).alterCurrentPage(navigationPage.getNumberPage() -1);
	}
	
	public void next(PaginationPage navigationPage) {
		((PagedListDataModel<?>) dataModel).alterCurrentPage(navigationPage.getNumberPage() +1);
	}

	public Collection<PaginationPage> getPaginationPages() {
		init();
		return paginationPages;
	}

	public void setPaginationPages(Collection<PaginationPage> paginationPages) {
		this.paginationPages = paginationPages;
	}

	public String getDataTable() {
		return dataTable;
	}

	public void setDataTable(String dataTable) {
		this.dataTable = dataTable;
	}

}
