/*
 * Copyright 2009-2014 PrimeTek.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.primefaces.component.paginator;

import java.io.IOException;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.component.api.UIData;

public class PageLinksRenderer implements PaginatorElementRenderer {

    public void render(FacesContext context, UIData uidata) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        int currentPage = uidata.getPage();
        int pageLinks = uidata.getPageLinks();
        int pageCount = uidata.getPageCount();
        int visiblePages = Math.min(pageLinks, pageCount);
        
        //calculate range, keep current in middle if necessary
        int start = Math.max(0, (int) Math.ceil(currentPage - ((visiblePages) / 2)));
        int end = Math.min(pageCount - 1, start + visiblePages - 1);
        
        //check when approaching to last page
        int delta = pageLinks - (end - start + 1);
        start = Math.max(0, start - delta);

        writer.startElement("span", null);
        writer.writeAttribute("class", "ui-paginator-pages", null);
        
        for(int i = start; i <= end; i++){
            String styleClass = currentPage == i ? "ui-paginator-page ui-state-active" : "ui-paginator-page";
            
            writer.startElement("span", null);
            writer.writeAttribute("class", styleClass, null);
            writer.writeAttribute("tabindex", 0, null);
            writer.writeText((i + 1), null);
            writer.endElement("span");
        }
            
        writer.endElement("span");
    }
}
