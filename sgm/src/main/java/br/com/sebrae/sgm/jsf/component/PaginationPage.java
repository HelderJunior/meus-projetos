package br.com.sebrae.sgm.jsf.component;

public class PaginationPage {
	
	private Integer numberPage;
	private Boolean active;
	private Long totalPages;

	public PaginationPage(Integer numberPage, Boolean active, Long totalPages) {
		this.numberPage = numberPage;
		this.active = active; 
		this.totalPages = totalPages;
	}

	public Integer getNumberPage() {
		return numberPage;
	}

	public void setNumberPage(Integer numberPage) {
		this.numberPage = numberPage;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public Long getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(Long totalPages) {
		this.totalPages = totalPages;
	}
	
	
	
}
