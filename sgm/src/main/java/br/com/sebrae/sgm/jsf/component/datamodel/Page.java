package br.com.sebrae.sgm.jsf.component.datamodel;

public class Page {

	private Integer numPage;
	private Integer numRecordsPerPage;
	
	public Page(Integer numPage, Integer numRecordsPerPage) {
		this.numPage = numPage;
		this.numRecordsPerPage = numRecordsPerPage;
	}
	
	public Integer getNumPage() {
		return numPage;
	}
	
	public Integer getNumRecordsPerPage() {
		return numRecordsPerPage;
	}
}
