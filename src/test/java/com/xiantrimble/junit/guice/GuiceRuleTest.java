package com.xiantrimble.junit.guice;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.junit.Rule;
import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Provides;

import static org.hamcrest.Matchers.*;

import java.util.function.Supplier;

import static org.hamcrest.MatcherAssert.*;
import org.junit.experimental.runners.Enclosed;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runners.model.Statement;

@RunWith(Enclosed.class)
public class GuiceRuleTest {
  public static class BasicUsage {
  public @Rule GuiceRule guiceRule = GuiceRule.builder()
      .withTarget(this)
      .addModule(OneModule::new)
      .addModule(TwoModule::new)
      .addModule(ThreeModule::new)
      .build();
  
  public @Inject @Named("one") Integer one;
  public @Inject @Named("two") Integer two;
  public @Named("three") Integer three;
  
  @Test public void oneInjected() {
    assertThat(one, equalTo(1));
  }
  
  @Test public void twoInjected() {
    assertThat(two, equalTo(2));
  }
  
  @Test public void threeNotInjected() {
    assertThat(three, nullValue());
  }
  }
  
  public static class ModuleSupplier {
    ModuleRule mr = new ModuleRule(OneModule::new);
    GuiceRule gr = GuiceRule.builder()
        .withTarget(this)
        .addModule(mr::getModule)
        .build();
    
    @Rule public RuleChain ruleChain = RuleChain
        .outerRule(mr)
        .around(gr);
    
    @Inject
    @Named("one")
    Integer value;
    
    @Test
    public void valueSet() {
      assertThat(value, equalTo(1));
    }
  }
}
  
  class ModuleRule implements TestRule {
    
    Supplier<Module> supplier;
    
    public ModuleRule( Supplier<Module> supplier ) {
      this.supplier = supplier;
    }

    Module module;
    
    public Module getModule() {
      return module;
    }

    @Override
    public Statement apply(Statement base, Description description) {
      return new Statement() {

        @Override
        public void evaluate() throws Throwable {
          try {
            module = supplier.get();
            base.evaluate();
          } finally {
            module = null;
          }
        }
        
      };
    }
    
  }
  
  class OneModule extends AbstractModule {
    @Override protected void configure() {}
    
    @Provides @Singleton @Named("one")
    public Integer provideOne() {
      return new Integer(1);
    }
  }
  
  class TwoModule extends AbstractModule {
    @Override protected void configure() {}

    @Provides @Singleton @Named("two")
    public Integer provideTwo() {
      return new Integer(2);
    }
  }
  
  class ThreeModule extends AbstractModule {
    @Override protected void configure() {}

    @Provides @Singleton @Named("three")
    public Integer provideThree() {
      return new Integer(3);
    }
  }
