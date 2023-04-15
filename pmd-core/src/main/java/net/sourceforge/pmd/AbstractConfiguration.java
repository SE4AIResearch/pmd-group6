/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import java.nio.charset.Charset;

/**
 * Base configuration class for both PMD and CPD.
 *
 * @author Brian Remedios
 */
public abstract class AbstractConfiguration {

    private Charset sourceEncoding = Charset.defaultCharset();
    private boolean debug;

    /**
     * Create a new abstract configuration.
     */
    protected AbstractConfiguration() {
        super();
    }

    /**
     * Get the character encoding of source files.
     *
     * @return The character encoding.
     */
    public Charset getSourceEncoding() {
        return sourceEncoding;
    }

    /**
     * Set the character encoding of source files.
     *
     * @param sourceEncoding
     *            The character encoding.
     * @return This configuration instance for method chaining.
     */
    public AbstractConfiguration withSourceEncoding(String sourceEncoding) {
        this.sourceEncoding = Charset.forName(sourceEncoding);
        return this;
    }

    /**
     * Return the debug indicator. If this value is <code>true</code> then PMD
     * will log debug information.
     *
     * @return <code>true</code> if debug logging is enabled, <code>false</code>
     *         otherwise.
     */
    public boolean isDebug() {
        return debug;
    }

    /**
     * Set the debug indicator.
     *
     * @param debug
     *            The debug indicator to set.
     * @return This configuration instance for method chaining.
     * @see #isDebug()
     */
    public AbstractConfiguration withDebug(boolean debug) {
        this.debug = debug;
        return this;
    }
}

