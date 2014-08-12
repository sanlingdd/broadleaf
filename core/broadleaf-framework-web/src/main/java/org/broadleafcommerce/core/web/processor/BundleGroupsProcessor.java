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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
 * A Thymeleaf processor that will add a List of {@link org.broadleafcommerce.core.web.processor.BundleGroupsDTO} to the
 * model.
 *
 * @param productBundleId the name of the order, {@link org.broadleafcommerce.core.catalog.domain.ProductBundle#getId()}
 * @param resultVar the value that the list will be assigned to
 *
 * @author Jerry Ocanas (jocanas)
 */
public class BundleGroupsProcessor extends AbstractModelVariableModifierProcessor {

    private static final Log LOG = LogFactory.getLog(BundleGroupsProcessor.class);

    private static final Integer DEFAULT_GROUP_ORDER = 999999999;
    private static final String DEFAULT_GROUP_NAME = "Group";
    private static final String DEFAULT_ENCODING = "UTF-8";

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

        // Find the bundle and add to a list of BundleGroupDTOs
        HashMap<String, BundleGroupsDTO> hashGroupDTOs = new HashMap<String, BundleGroupsDTO>();
        Product product = catalogService.findProductById(productBundleId);
        if (product != null && product instanceof ProductBundle) {
            List<SkuBundleItem> bundleItems = ((ProductBundle) product).getSkuBundleItems();
            if (!bundleItems.isEmpty()) {
                // Loop through the SkuBundle items
                for (SkuBundleItem item : bundleItems) {
                    // Check if the group has already been added
                    String groupKey = item.getGroupName();
                    BundleGroupsDTO groupDTO =
                            hashGroupDTOs.containsKey(groupKey) ? hashGroupDTOs.get(item.getGroupName()) :
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
                    String groupName = item.getGroupName() == null ? DEFAULT_GROUP_NAME : item.getGroupName();
                    groupDTO.setGroupName(groupName);
                    try {
                        groupDTO.setGroupLink(URLEncoder.encode(groupName.replace(" ", ""), DEFAULT_ENCODING));
                    } catch (UnsupportedEncodingException e) {
                        LOG.error("Failed to encode [" + groupName + "] for ["+ item + "]; Only removing spaces.", e);
                        groupDTO.setGroupLink(groupName.replace(" ", ""));
                    }
                    groupDTO.setHeaders(headers);
                    groupDTO.getItems().add(item);
                    groupDTO.setGroupOrder(item.getGroupOrder() == null ? DEFAULT_GROUP_ORDER : item.getGroupOrder());
                    hashGroupDTOs.put(item.getGroupName(), groupDTO);
                }
            }

            if (!hashGroupDTOs.isEmpty()) {
                List<BundleGroupsDTO> groups = new ArrayList<BundleGroupsDTO>(hashGroupDTOs.values());
                Collections.sort(groups, new Comparator<BundleGroupsDTO>() {
                    @Override
                    public int compare(BundleGroupsDTO o1, BundleGroupsDTO o2) {
                        return o1.getGroupOrder().compareTo(o2.getGroupOrder());
                    }
                });
                addToModel(arguments, resultVar, groups);
            } else {
                // Return an empty list if no results were found
                addToModel(arguments, resultVar, new ArrayList<BundleGroupsDTO>());
            }
        }
    }
}
