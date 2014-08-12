/*
 * #%L
 * BroadleafCommerce Framework Web
 * %%
 * Copyright (C) 2009 - 2014 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.broadleafcommerce.core.web.processor;

import java.util.ArrayList;
import java.util.List;

import org.broadleafcommerce.core.catalog.domain.SkuBundleItem;

/**
 * A Generic DTO object that represents the information to display a Bundle Group.
 *
 * Used on the front end as a way to easily iterate over bundle groups and the data displayed within the group.
 *
 * @author Jerry Ocanas (jocanas)
 */
public class BundleGroupsDTO {

    public String groupName;
    public String groupLink;
    public Integer groupOrder;
    public List<String> headers = new ArrayList<String>();
    public Boolean showQuantity = false;
    public List<SkuBundleItem> items = new ArrayList<SkuBundleItem>();

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupLink() {
        return groupLink;
    }

    public void setGroupLink(String groupLink) {
        this.groupLink = groupLink;
    }

    public Integer getGroupOrder() {
        return groupOrder;
    }

    public void setGroupOrder(Integer groupOrder) {
        this.groupOrder = groupOrder;
    }

    public List<String> getHeaders() {
        return headers == null ? new ArrayList<String>() : headers;
    }

    public void setHeaders(List<String> headers) {
        this.headers = headers;
    }

    public Boolean getShowQuantity() {
        return showQuantity == null ? Boolean.FALSE : showQuantity;
    }

    public void setShowQuantity(Boolean showQuantity) {
        this.showQuantity = showQuantity;
    }

    public List<SkuBundleItem> getItems() {
        return items == null ? new ArrayList<SkuBundleItem>() : items;
    }

    public void setItems(List<SkuBundleItem> items) {
        this.items = items;
    }
}
