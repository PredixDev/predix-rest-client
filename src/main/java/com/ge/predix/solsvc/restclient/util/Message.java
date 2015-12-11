
package com.ge.predix.solsvc.restclient.util;

import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/**
 * Message
 * <p>
 * Error message returned in case of error
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "message",
    "errors"
})
public class Message {

    /**
     * message
     * (Required)
     * 
     */
    @JsonProperty("message")
    private String message;
    /**
     * List of error and reason
     * (Required)
     * 
     */
    @JsonProperty("errors")
    private Object errors;

    /**
     * message
     * (Required)
     * @return -
     * 
     */
    @JsonProperty("message")
    public String getMessage() {
        return this.message;
    }

    /**
     * message
     * (Required)
     * @param message -
     * 
     */
    @JsonProperty("message")
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * List of error and reason
     * (Required)
     * @return -
     * 
     */
    @JsonProperty("errors")
    public Object getErrors() {
        return this.errors;
    }

    /**
     * List of error and reason
     * (Required)
     * @param errors -
     * 
     */
    @JsonProperty("errors")
    public void setErrors(Object errors) {
        this.errors = errors;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(Object other) {
        return EqualsBuilder.reflectionEquals(this, other);
    }

}
