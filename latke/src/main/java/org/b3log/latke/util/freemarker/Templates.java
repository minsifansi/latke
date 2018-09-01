/*
 * Copyright (c) 2009-2018, b3log.org & hacpai.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.b3log.latke.util.freemarker;

import freemarker.core.TemplateElement;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;

import java.io.IOException;
import java.util.Enumeration;

/**
 * Utilities of <a href="http://www.freemarker.org">FreeMarker</a>
 * template engine.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.1.3, May 22, 2012
 */
public final class Templates {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(Templates.class);

    /**
     * Main template {@link Configuration configuration}.
     */
    public static final Configuration MAIN_CFG = new Configuration();

    /**
     * Mobile template {@link Configuration configuration}.
     */
    public static final Configuration MOBILE_CFG = new Configuration();

    static {
        MAIN_CFG.setDefaultEncoding("UTF-8");
        MOBILE_CFG.setDefaultEncoding("UTF-8");
    }

    /**
     * Private constructor.
     */
    private Templates() {
    }

    /**
     * Determines whether exists a variable specified by the given expression
     * in the specified template.
     *
     * @param template   the specified template
     * @param expression the given expression, for example,
     *                   "${aVariable}", "&lt;#list recentComments as comment&gt;"
     * @return {@code true} if it exists, returns {@code false} otherwise
     */
    public static boolean hasExpression(final Template template, final String expression) {
        final TemplateElement rootTreeNode = template.getRootTreeNode();

        return hasExpression(template, expression, rootTreeNode);
    }

    /**
     * Determines whether the specified expression exists in the specified
     * element (includes its children) of the specified template.
     *
     * @param template        the specified template
     * @param expression      the specified expression
     * @param templateElement the specified element
     * @return {@code true} if it exists, returns {@code false} otherwise
     */
    private static boolean hasExpression(final Template template,
                                         final String expression,
                                         final TemplateElement templateElement) {
        final String canonicalForm = templateElement.getCanonicalForm();

        if (canonicalForm.startsWith(expression)) {
            LOGGER.log(Level.TRACE, "Template has expression[nodeName={0}, expression={1}]",
                    new Object[]{templateElement.getNodeName(), expression});

            return true;
        }

        final Enumeration<TemplateElement> children = templateElement.children();

        while (children.hasMoreElements()) {
            final TemplateElement nextElement = children.nextElement();

            if (hasExpression(template, expression, nextElement)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Gets a FreeMarker {@linkplain Template template} with the specified
     * template directory name and template name.
     *
     * @param templateDirName the specified template directory name
     * @param templateName    the specified template name
     * @return a template, returns {@code null} if not found
     */
    public static Template getTemplate(final String templateDirName, final String templateName) {
        try {
            try {
                if ("mobile".equals(templateDirName)) {
                    return MOBILE_CFG.getTemplate(templateName);
                }
            } catch (final Exception e) {
                LOGGER.log(Level.ERROR, "Can not load mobile template[templateDirName={0}, templateName={1}]",
                        new Object[]{templateDirName, templateName});
                return null;
            }

            return MAIN_CFG.getTemplate(templateName);
        } catch (final IOException e) {
            LOGGER.log(Level.WARN, "Gets template[name={0}] failed: [{1}]", new Object[]{templateName, e.getMessage()});

            return null;
        }
    }
}
