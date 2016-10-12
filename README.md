# JUnit Guice

Tools for using Guice with JUnit

## JUnit 4 Rule

To use the GuiceRule, first import it in your test.

```
import com.xiantrimble.junit.guice.GuiceRule;
```

Then build a new instance of the rule in your test, passing in the instance of the test and suppliers for the
modules you wish to use.

```
  @Rule
  public GuiceRule guiceRule = GuiceRule.builder()
      .withTarget(this)
      .addModule(MyModule::new)
      .build();
```

Then define some fields to inject

```
  @Inject
  public MyType myValue;
```

You can use the GuiceRule with other rules using the RuleChain.

```
import org.junit.rules.RuleChain;
```

```
  public GuiceRule guiceRule = ...;
  public OtherRule otherRule = ...;
  
  @Rule
  public RuleChain ruleChain = RuleChain
        .outerRule(otherRule)
        .around(guiceRule);
```

[![Build Status](https://secure.travis-ci.org/ctrimble/junit-guice.png?branch=develop)](https://travis-ci.org/ctrimble/junit-guice)
