package br.com.sebrae.sgm.jsf.component.datamodel;

import java.util.List;

import javax.faces.model.ListDataModel;

public class PagedListDataModel<Entity> extends ListDataModel<Entity> { 

	private Page page;

	public PagedListDataModel(Page page, List<Entity> list) {
		super(list);
		this.page = page;
	}
	
	@Override
	public boolean isRowAvailable() {
		if (getPage().getNumPage().equals(1)) {
			return super.isRowAvailable();
		} else {
			List<?> list = (List<?>) getWrappedData();
	        return (list != null && getRowIndex() >= 0 && 
	        		((getRowIndex() + (getPage().getNumRecordsPerPage() * (getCurrentPage() - 1))) < list.size()));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Entity getRowData() {
		List<?> list = (List<?>) getWrappedData();
        if (list == null) {
        	return null;
        } else if (!isRowAvailable()) {
            throw new RuntimeException();
        } else {
    		if (getPage().getNumPage().equals(1)) {
    			return super.getRowData();
    		} else {
    	        return (Entity) list.get((getRowIndex() + (getPage().getNumRecordsPerPage() * (getCurrentPage() - 1))));
    		}
        }
	}

	public Page getPage() {
		return page;
	}
	
	public void setPage(Page page) {
		this.page = page;
	}
	
	public void alterCurrentPage(Integer numPage) {
		if (!page.getNumPage().equals(numPage)) {
			this.page = new Page(numPage, this.page.getNumRecordsPerPage());
		}
	}
	
	@Override
	public void setWrappedData(Object list) {
		if (getPage() == null) {
			setPage(new Page(1, 10));
		}
		super.setWrappedData(list);
	}
	
	public Long getTotalPage() {
		if (getRowCount() <= getPage().getNumRecordsPerPage()) {
			return 1l;
		} else {
			double mod = getRowCount() % getPage().getNumRecordsPerPage(); 
			if (mod == 0) {
				return (long) (getRowCount() / 
						getPage().getNumRecordsPerPage());
			} else {
				return (long) (getRowCount() / 
						getPage().getNumRecordsPerPage()) + 1;
			}
		}
	}

	public Integer getCurrentPage() {
		return getPage().getNumPage();
	}
	
}