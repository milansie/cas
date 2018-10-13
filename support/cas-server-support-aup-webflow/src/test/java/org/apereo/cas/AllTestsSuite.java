
package org.apereo.cas;

import org.apereo.cas.web.flow.AcceptableUsagePolicySubmitActionTests;
import org.apereo.cas.web.flow.AcceptableUsagePolicyVerifyActionTests;
import org.apereo.cas.aup.DefaultAcceptableUsagePolicyRepositoryTests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * This is {@link AllTestsSuite}.
 *
 * @author Auto-generated by Gradle Build
 * @since 6.0.0-RC3
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
    AcceptableUsagePolicySubmitActionTests.class,
    AcceptableUsagePolicyVerifyActionTests.class,
    DefaultAcceptableUsagePolicyRepositoryTests.class
})
public class AllTestsSuite {
}
