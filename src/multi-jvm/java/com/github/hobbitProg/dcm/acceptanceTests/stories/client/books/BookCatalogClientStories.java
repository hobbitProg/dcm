package com.github.hobbitProg.dcm.acceptanceTests.stories.client.books;

import java.util.List;
import org.jbehave.core.configuration.Configuration;
import org.jbehave.core.configuration.MostUsefulConfiguration;
import org.jbehave.core.configuration.scala.ScalaContext;
import org.jbehave.core.io.CodeLocations;
import org.jbehave.core.io.StoryFinder;
import org.jbehave.core.junit.JUnitStories;
import org.jbehave.core.reporters.Format;
import org.jbehave.core.reporters.StoryReporterBuilder;
import org.jbehave.core.steps.InjectableStepsFactory;
import org.jbehave.core.steps.scala.ScalaStepsFactory;

/**
 * Maps stories for book catalog stories to respective steps
 * @author Kyle Cranmer
 * @since 0.1
 */
public class BookCatalogClientStories extends JUnitStories {
  /**
   * Create configuration to use in running stories for allowing books to be
   * added to catalog
   * @return Configuration to use in running stories for allowing books to be
   * added to catalog
   * @since 0.1
   */
  @Override
  public Configuration configuration() {
    return
      new MostUsefulConfiguration().useStoryReporterBuilder(
        new StoryReporterBuilder().withFormats(
          Format.HTML
        )
      );
  }

  /**
   * Retrieve where stories for book catalog client are located
   * @return Where stories for book catalog client are located
   * @since 0.1
   */
  @Override
  protected List<String> storyPaths() {
    return
      new StoryFinder().findPaths(
        CodeLocations.codeLocationFromClass(
          this.getClass()
        ),
        "stories/client/books/*.story",
        ""
      );
  }

  /**
   * Retrieve factory to create steps of story
   * @return Factory to create steps of story
   * @since 0.1
   */
  @Override
  public InjectableStepsFactory stepsFactory() {
    return
      new ScalaStepsFactory(
        configuration(),
        new ScalaContext(
          "BookCatalogClientSteps"
        )
      );
  }
}
