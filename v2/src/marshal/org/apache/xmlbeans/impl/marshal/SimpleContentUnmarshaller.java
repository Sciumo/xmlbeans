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

package org.apache.xmlbeans.impl.marshal;

import org.apache.xmlbeans.XmlException;

public class SimpleContentUnmarshaller
    extends AttributeUnmarshaller
{

    private final SimpleContentRuntimeBindingType simpleContentRuntimeBindingType;

    public SimpleContentUnmarshaller(SimpleContentRuntimeBindingType type)
    {
        super(type);
        simpleContentRuntimeBindingType = type;
    }


    //TODO: cleanup this code.  We are doing extra work for assertion checking
    protected void deserializeContents(Object inter,
                                       UnmarshalResult context)
        throws XmlException
    {
        RuntimeBindingProperty scprop =
            simpleContentRuntimeBindingType.getSimpleContentProperty();
        UnmarshalResult.fillElementProp(scprop, context, inter);
    }


}
