/* Copyright 2010-2020 Norconex Inc.
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
package com.norconex.importer.handler.filter;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.norconex.commons.lang.xml.IXMLConfigurable;
import com.norconex.commons.lang.xml.XML;
import com.norconex.importer.doc.ImporterMetadata;
import com.norconex.importer.handler.AbstractImporterHandler;
import com.norconex.importer.handler.ImporterHandlerException;

/**
 * <p>Base class for filters dealing with the body of text documents only.
 * Subclasses can safely be used as either pre-parse or post-parse handlers
 * restricted to text documents only (see {@link AbstractImporterHandler}).
 * </p>
 *
 * <p>When used as a pre-parse handler,
 * this class attempts to detect the content character
 * encoding unless the character encoding
 * was specified using {@link #setSourceCharset(String)}. Since document
 * parsing converts content to UTF-8, UTF-8 is always assumed when
 * used as a post-parse handler.
 * </p>
 *
 * {@nx.xml.usage #attributes
 *  sourceCharset="(character encoding)"
 *  {@nx.include com.norconex.importer.handler.filter.AbstractDocumentFilter#attributes}
 * }
 *
 * <p>
 * Subclasses inherit the above {@link IXMLConfigurable} attribute(s),
 * in addition to <a href="../AbstractImporterHandler.html#nx-xml-restrictTo">
 * &lt;restrictTo&gt;</a>.
 * </p>
 *
 * @author Pascal Essiembre
 * @since 2.0.0
 */
@SuppressWarnings("javadoc")
public abstract class AbstractCharStreamFilter extends AbstractDocumentFilter {

    private String sourceCharset = null;

    /**
     * Gets the assumed source character encoding.
     * @return character encoding of the source to be transformed
     * @since 2.5.0
     */
    public String getSourceCharset() {
        return sourceCharset;
    }
    /**
     * Sets the assumed source character encoding.
     * @param sourceCharset character encoding of the source to be transformed
     * @since 2.5.0
     */
    public void setSourceCharset(String sourceCharset) {
        this.sourceCharset = sourceCharset;
    }

    @Override
    protected final boolean isDocumentMatched(
            String reference, InputStream input,
            ImporterMetadata metadata, boolean parsed)
            throws ImporterHandlerException {

        String inputCharset = detectCharsetIfBlank(
                sourceCharset, reference, input, metadata, parsed);
        try {
            InputStreamReader is = new InputStreamReader(input, inputCharset);
            return isTextDocumentMatching(reference, is, metadata, parsed);
        } catch (UnsupportedEncodingException e) {
            throw new ImporterHandlerException(e);
        }
    }

    protected abstract boolean isTextDocumentMatching(
            String reference, Reader input,
            ImporterMetadata metadata, boolean parsed)
            throws ImporterHandlerException;


    @Override
    protected final void saveFilterToXML(XML xml) {
        xml.setAttribute("sourceCharset", sourceCharset);
        saveCharStreamFilterToXML(xml);
    }
    /**
     * Saves configuration settings specific to the implementing class.
     * The parent tag along with the "class" attribute are already written.
     * Implementors must not close the writer.
     *
     * @param xml the XML
     */
    protected abstract void saveCharStreamFilterToXML(XML xml);


    @Override
    protected final void loadFilterFromXML(XML xml) {
        setSourceCharset(xml.getString("@sourceCharset", sourceCharset));
        loadCharStreamFilterFromXML(xml);
    }
    /**
     * Loads configuration settings specific to the implementing class.
     * @param xml XML configuration
     */
    protected abstract void loadCharStreamFilterFromXML(XML xml);


    @Override
    public boolean equals(final Object other) {
        return EqualsBuilder.reflectionEquals(this, other);
    }
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
    @Override
    public String toString() {
        return new ReflectionToStringBuilder(this,
                ToStringStyle.SHORT_PREFIX_STYLE).toString();
    }
}