/*   Copyright 2004 The Apache Software Foundation
*
*   Licensed under the Apache License, Version 2.0 (the "License");
*   you may not use this file except in compliance with the License.
*   You may obtain a copy of the License at
*
*       http://www.apache.org/licenses/LICENSE-2.0
*
*   Unless required by applicable law or agreed to in writing, software
*   distributed under the License is distributed on an "AS IS" BASIS,
*   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*   See the License for the specific language governing permissions and
*  limitations under the License.
*/

package org.apache.xmlbeans;

import javax.xml.namespace.QName;

/**
 * The BindingConfig class is used during compilation to control the generation of java source files.
 * The default BindingConfig does nothing, but sub-classes should provide more interesting behavior.
 *
 * @see {@link XmlBeans#compileXmlBeans(String, SchemaTypeSystem, XmlObject[], BindingConfig, SchemaTypeLoader, Filer, XmlOptions) XmlBeans.compileXmlBeans()}
 */
public class BindingConfig
{
    private static final InterfaceExtension[] EMPTY_INTERFACE_EXT_ARRAY = new InterfaceExtension[0];
    private static final PrePostExtension[] EMPTY_PREPOST_EXT_ARRAY = new PrePostExtension[0];

    /**
     * Get the package name for a namespace or null.
     */
    public String lookupPackageForNamespace(String uri) { return null; }

    /**
     * Get the prefix applied to each java name for a namespace or null.
     */
    public String lookupPrefixForNamespace(String uri) { return null; }

    /**
     * Get the suffix applied to each java name for a namespace or null.
     */
    public String lookupSuffixForNamespace(String uri) { return null; }

    /**
     * Get the java name for a QName or null.
     */
    public String lookupJavanameForQName(QName qname) { return null; }

    /**
     * Returns all configured InterfaceExtensions or an empty array.
     */
    public InterfaceExtension[] getInterfaceExtensions() { return EMPTY_INTERFACE_EXT_ARRAY; }

    /**
     * Returns all InterfaceExtensions defined for the fully qualified java
     * type generated from schema compilation or an empty array.
     */
    public InterfaceExtension[] getInterfaceExtensions(String fullJavaName) { return EMPTY_INTERFACE_EXT_ARRAY; }

    /**
     * Returns all configued PrePostExtensions or an empty array.
     */
    public PrePostExtension[] getPrePostExtensions() { return EMPTY_PREPOST_EXT_ARRAY; }

    /**
     * Returns the PrePostExtension defined for the fully qualified java
     * type generated from schema compilation or null.
     */
    public PrePostExtension getPrePostExtension(String fullJavaName) { return null; }

}
