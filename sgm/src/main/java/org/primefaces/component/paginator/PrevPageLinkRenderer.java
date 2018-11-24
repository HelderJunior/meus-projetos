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

public class PrevPageLinkRenderer extends PageLinkRenderer implements PaginatorElementRenderer {

    public void render(FacesContext context, UIData uidata) throws IOException {
        boolean disabled = uidata.getPage() == 0;
       
        ResponseWriter writer = context.getResponseWriter();
        String styleClass = disabled ? "ui-paginator-prev" + " ui-state-disabled" : "ui-paginator-prev";

        writer.startElement("span", null);
        writer.writeAttribute("class", styleClass, null);
        if(!disabled) {
            writer.writeAttribute("tabindex", 0, null);
        }
        
        writer.startElement("span", null);
        writer.writeText("Anterior", null);
        writer.endElement("span");
        
        writer.endElement("span");
    }
}