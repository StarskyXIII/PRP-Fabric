package net.starskyxiii.prp;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PRP implements ModInitializer
{
	public static final Logger LOGGER = LoggerFactory.getLogger("prp");

	@Override
	public void onInitialize()
	{
		LOGGER.info("Hello, this is PRP!");
	}
}
