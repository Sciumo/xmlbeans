/*
* The Apache Software License, Version 1.1
*
*
* Copyright (c) 2003 The Apache Software Foundation.  All rights
* reserved.
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions
* are met:
*
* 1. Redistributions of source code must retain the above copyright
*    notice, this list of conditions and the following disclaimer.
*
* 2. Redistributions in binary form must reproduce the above copyright
*    notice, this list of conditions and the following disclaimer in
*    the documentation and/or other materials provided with the
*    distribution.
*
* 3. The end-user documentation included with the redistribution,
*    if any, must include the following acknowledgment:
*       "This product includes software developed by the
*        Apache Software Foundation (http://www.apache.org/)."
*    Alternately, this acknowledgment may appear in the software itself,
*    if and wherever such third-party acknowledgments normally appear.
*
* 4. The names "Apache" and "Apache Software Foundation" must
*    not be used to endorse or promote products derived from this
*    software without prior written permission. For written
*    permission, please contact apache@apache.org.
*
* 5. Products derived from this software may not be called "Apache
*    XMLBeans", nor may "Apache" appear in their name, without prior
*    written permission of the Apache Software Foundation.
*
* THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
* WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
* OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
* DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
* ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
* SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
* LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
* USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
* ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
* OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
* OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
* SUCH DAMAGE.
* ====================================================================
*
* This software consists of voluntary contributions made by many
* individuals on behalf of the Apache Software Foundation and was
* originally based on software copyright (c) 2000-2003 BEA Systems
* Inc., <http://www.bea.com/>. For more information on the Apache Software
* Foundation, please see <http://www.apache.org/>.
*/

package org.apache.xmlbeans.impl.marshal;

import org.apache.xmlbeans.impl.binding.bts.ByNameBean;

import javax.xml.namespace.QName;
import java.util.List;
import java.util.ArrayList;

class ByNameTypeVisitor extends NamedXmlTypeVisitor
{
    private final ByNameRuntimeBindingType type;
    private final int maxPropCount;
    private int propIdx = -1;
    private List attributeNames;
    private List attributeValues;


    ByNameTypeVisitor(RuntimeBindingProperty property, Object obj,
                      MarshalContext context)
    {
        super(obj, property, context);
        final ByNameBean bean_type = (ByNameBean)property.getType();
        //TODO: avoid new
        type = new ByNameRuntimeBindingType(bean_type);
        type.initialize(context.getTypeTable(), context.getLoader());

        maxPropCount = obj == null ? 0 : type.getPropertyCount();
    }

    protected int getState()
    {
        assert propIdx <= maxPropCount; //ensure we don't go past the end

        if (propIdx < 0) return START;

        if (propIdx >= maxPropCount) return END;

        return CONTENT;
    }

    protected int advance()
    {
        assert propIdx < maxPropCount; //ensure we don't go past the end
        ++propIdx;

        if (propIdx == maxPropCount)
            return END;


        final RuntimeBindingProperty property = getCurrentProperty();

        if (property.isAttribute() || !property.isSet(parentObject, marshalContext))
            return advance();

        return getState();
    }

    public XmlTypeVisitor getCurrentChild()
    {
        final RuntimeBindingProperty property = getCurrentProperty();
        Object prop_obj = property.getValue(parentObject, marshalContext);
        return MarshalResult.createVisitor(property, prop_obj, marshalContext);
    }

    private RuntimeBindingProperty getCurrentProperty()
    {
        final RuntimeBindingProperty property = type.getProperty(propIdx);
        assert property != null;
        return property;
    }

    protected int getAttributeCount()
    {
        if (attributeValues == null) initAttributes();

        assert attributeNames.size() == attributeValues.size();

        return attributeValues.size();
    }

    protected void initAttributes()
    {
        assert attributeNames == null;
        assert attributeValues == null;

        final List vals = new ArrayList();
        final List names = new ArrayList();

        if (parentObject == null) {
            QName nil_qn = fillPrefix(MarshalStreamUtils.XSI_NIL_QNAME);
            names.add(nil_qn);
            vals.add("true");
        } else {
            //TODO: xsi:type for polymorphism

            for (int i = 0, len = maxPropCount; i < len; i++) {
                final RuntimeBindingProperty prop = type.getProperty(i);

                if (!prop.isAttribute()) continue;
                if (!prop.isSet(parentObject, marshalContext)) continue;

                final Object value = prop.getValue(parentObject, marshalContext);

                final CharSequence val = prop.getLexical(value,
                                                         marshalContext);

                if (val == null) continue;

                vals.add(val);
                names.add(fillPrefix(prop.getName()));
            }
        }

        attributeNames = names;
        attributeValues = vals;

        assert attributeNames.size() == attributeValues.size();
    }

    protected String getAttributeValue(int idx)
    {
        CharSequence val = (CharSequence)attributeValues.get(idx);
        return val.toString();
    }

    protected QName getAttributeName(int idx)
    {
        QName an = (QName)attributeNames.get(idx);

        //make sure we have a valid prefix
        assert ((an.getPrefix().length() == 0) ==
            (an.getNamespaceURI().length() == 0));

        return an;
    }

    protected CharSequence getCharData()
    {
        throw new IllegalStateException("not text: " + this);
    }

}