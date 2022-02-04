package org.apereo.cas.support.oauth.web.response.accesstoken.ext;

import org.apereo.cas.support.oauth.OAuth20Constants;
import org.apereo.cas.support.oauth.util.OAuth20Utils;
import org.apereo.cas.support.oauth.web.endpoints.OAuth20ConfigurationContext;

import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.pac4j.core.context.WebContext;

/**
 * This is {@link AccessTokenProofKeyCodeExchangeAuthorizationCodeGrantRequestExtractor}.
 *
 * @author Misagh Moayyed
 * @since 6.0.0
 */
public class AccessTokenProofKeyCodeExchangeAuthorizationCodeGrantRequestExtractor
    extends AccessTokenAuthorizationCodeGrantRequestExtractor {
    public AccessTokenProofKeyCodeExchangeAuthorizationCodeGrantRequestExtractor(final OAuth20ConfigurationContext oAuthConfigurationContext) {
        super(oAuthConfigurationContext);
    }

    @Override
    public boolean supports(final WebContext context) {
        val challenge = OAuth20Utils.getRequestParameter(context, OAuth20Constants.CODE_VERIFIER).orElse(StringUtils.EMPTY);
        return StringUtils.isNotBlank(challenge) && super.supports(context);
    }

    @Override
    public boolean requestMustBeAuthenticated() {
        return true;
    }

    @Override
    protected AccessTokenRequestContext extractInternal(
        final WebContext context,
        final AccessTokenRequestContext.AccessTokenRequestContextBuilder builder) {
        val challenge = OAuth20Utils.getRequestParameter(context, OAuth20Constants.CODE_VERIFIER).orElse(StringUtils.EMPTY);
        return builder.codeVerifier(challenge).build();
    }
}
