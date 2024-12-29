package consulo.images.ui;

import consulo.annotation.component.ComponentScope;
import consulo.annotation.component.ServiceAPI;
import consulo.ui.Component;
import consulo.ui.image.Image;

import jakarta.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 05/12/2022
 */
@ServiceAPI(ComponentScope.APPLICATION)
public interface EmbeddedImageViewFactory
{
	@Nonnull
	Component createViewer(@Nonnull Image image);
}
