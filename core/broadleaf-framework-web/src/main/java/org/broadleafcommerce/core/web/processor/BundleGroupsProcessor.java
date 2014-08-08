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
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.broadleafcommerce.common.web.dialect.AbstractModelVariableModifierProcessor;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.ProductBundle;
import org.broadleafcommerce.core.catalog.domain.ProductOptionXref;
import org.broadleafcommerce.core.catalog.domain.SkuBundleItem;
import org.broadleafcommerce.core.catalog.service.CatalogService;
import org.thymeleaf.Arguments;
import org.thymeleaf.Configuration;
import org.thymeleaf.dom.Element;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressions;

/**
 * A Thymeleaf processor that will add a list of BundleGroupDTOs to the model.
 *
 * @author Jerry Ocanas (jocanas)
 */
public class BundleGroupsProcessor extends AbstractModelVariableModifierProcessor {

    @Resource(name = "blCatalogService")
    protected CatalogService catalogService;

    public BundleGroupsProcessor() {
        super("bundle_groups");
    }

    @Override
    public int getPrecedence() {
        return 10000;
    }

    @Override
    protected void modifyModelAttributes(Arguments arguments, Element element) {
        final Configuration configuration = arguments.getConfiguration();
        final IStandardExpressionParser parser = StandardExpressions.getExpressionParser(configuration);
        final String attributeValue = element.getAttributeValue("productBundleId");
        final IStandardExpression expression = parser.parseExpression(configuration, arguments, attributeValue);
        final Long productBundleId = (Long) expression.execute(configuration, arguments);
        final String resultVar = element.getAttributeValue("resultVar");

        HashMap<String, BundleGroupsDTO> groupsDTOs = new HashMap<String, BundleGroupsDTO>();

        // Find the bundle
        Product product = catalogService.findProductById(productBundleId);
        if (product != null && product instanceof ProductBundle) {
            List<SkuBundleItem> bundleItems = ((ProductBundle) product).getSkuBundleItems();
            if (!bundleItems.isEmpty()) {
                // Loop through the SkuBundle items
                for (SkuBundleItem item : bundleItems) {
                    // Check if the group has already been added
                    String groupKey = item.getGroupName();
                    BundleGroupsDTO groupDTO = groupsDTOs.containsKey(groupKey) ? groupsDTOs.get(item.getGroupName()) :
                            new BundleGroupsDTO();

                    groupDTO.setShowQuantity(groupDTO.getShowQuantity() || item.getQuantity() > 1);

                    // Add the the required fields to the header
                    List<String> headers = new ArrayList<String>();
                    headers.add("Name");
                    if (groupDTO.getShowQuantity()) {
                        headers.add("Qty");
                    }
                    for (ProductOptionXref option : item.getSku().getProduct().getProductOptionXrefs()) {
                        headers.add(option.getProductOption().getAttributeName());
                    }

                    groupDTO.setGroupName(item.getGroupName());
                    // TODO add order
//                    groupDTO.setGroupOrder(item.getGroupOrder());
                    groupDTO.setHeaders(headers);
                    groupDTO.getItems().add(item);
                    groupsDTOs.put(item.getGroupName(), groupDTO);
                }
            }

            if (!groupsDTOs.isEmpty()) {
                addToModel(arguments, resultVar, groupsDTOs);
            }
        }
    }
}
