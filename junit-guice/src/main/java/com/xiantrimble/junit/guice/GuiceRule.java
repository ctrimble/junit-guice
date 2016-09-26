package com.xiantrimble.junit.guice;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import com.google.common.collect.Lists;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

public class GuiceRule implements TestRule {
  
  public static class Builder {
    List<Supplier<Module>> modules = Lists.newArrayList();
    Object target;
    
    public Builder withTarget( Object target ) {
      this.target = target;
      return this;
    }
    
    public Builder addModule( Module module ) {
      modules.add(()->module);
      return this;
    }
    
    public Builder addModule( Supplier<Module> module ) {
      modules.add(module);
      return this;
    }
    
    public Builder with( Consumer<Builder> builderCommands ) {
      builderCommands.accept(this);
      return this;
    }

    public GuiceRule build() {
      return new GuiceRule(target, modules);
    }
  }
  
  public static Builder builder() { return new Builder(); }

  private Injector injector;
  private List<Supplier<Module>> modules;
  private Object target;

  public GuiceRule(Object target, List<Supplier<Module>> modules) {
    this.target = target;
    this.modules = Lists.newArrayList(modules);
  }
  
  public Injector getInjector() {
    return injector;
  }

  @Override
  public Statement apply(Statement base, Description description) {
    return new Statement() {
      @Override
      public void evaluate() throws Throwable {
        try {
          injector = Guice.createInjector(modules.stream()
              .map(Supplier::get)
              .collect(Collectors.toList()));
          injector.injectMembers(target);
          base.evaluate();
        }
        finally {
          injector = null;
        }
      }
    };
  }

}