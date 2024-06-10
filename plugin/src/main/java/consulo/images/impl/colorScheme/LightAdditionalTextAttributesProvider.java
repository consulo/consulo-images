package consulo.images.impl.colorScheme;

import consulo.annotation.component.ExtensionImpl;
import consulo.colorScheme.AdditionalTextAttributesProvider;
import consulo.colorScheme.EditorColorsScheme;
import jakarta.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 06/06/2024
 */
@ExtensionImpl
public class LightAdditionalTextAttributesProvider implements AdditionalTextAttributesProvider {
  @Nonnull
  @Override
  public String getColorSchemeName() {
    return EditorColorsScheme.DEFAULT_SCHEME_NAME;
  }
}
