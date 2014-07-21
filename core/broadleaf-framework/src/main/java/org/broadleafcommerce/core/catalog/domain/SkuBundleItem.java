/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
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
package org.broadleafcommerce.core.catalog.domain;

import org.broadleafcommerce.common.money.Money;

import java.io.Serializable;

/**
 * Represents the {@link org.broadleafcommerce.core.catalog.domain.Sku} being sold in a bundle along with metadata
 * about the relationship itself like how many items should be included in the
 * bundle
 *
 * @author Phillip Verheyden
 * @see ProductBundle, Product
 */
public interface SkuBundleItem extends Serializable {

    public Long getId();

    public void setId(Long id);

    public Integer getQuantity();

    public void setQuantity(Integer quantity);

    /**
    * Allows for overriding the related Product's sale price. This is only used
    * if the pricing model for the bundle is a composition of its parts
    * getProduct().getDefaultSku().getSalePrice()
    *
    * @param itemSalePrice The sale price for this bundle item
    */
    public void setSalePrice(Money salePrice);

    /**
    * @return this itemSalePrice if it is set,
    *         getProduct().getDefaultSku().getSalePrice() if this item's itemSalePrice is
    *         null
    */
    public Money getSalePrice();

    public ProductBundle getBundle();

    public void setBundle(ProductBundle bundle);

    public Money getRetailPrice();

    public Sku getSku();

    public void setSku(Sku sku);

    /**
     * Removes any currently stored dynamic pricing
     */
    public void clearDynamicPrices();

    /**
     * Will allow for the selection of a different Sku from the Parent product.
     *
     * @return true if the sku allows for the selection of a different Sku from the Parent product.
     */
    public Boolean getAllowOtherSkuFromParentProduct();

    public void setAllowOtherSkuFromParentProduct(Boolean allowOtherSkuFromParentProduct);

    /**
     * The short name used that can be used by the item for a more accessible name within the bundle.
     *
     * @return the short name
     */
    public String getShortName();

    public void setShortName(String shortName);

    /**
     * The group name which is used for displaying the item.
     *
     * For example, this would allow for separation of groups via tabs.
     *
     * @return the group name
     */
    public String getGroupName();

    public void setGroupName(String groupName);

    /**
     * The order in which the group assigned will be displayed.
     *
     * @return the group order
     */
    public Integer getGroupOrder();

    public void setGroupOrder(Integer groupOrder);
}
