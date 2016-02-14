/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015, 2016 IceDragon200
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package growthcraft.api.cellar.culture;

import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import growthcraft.api.core.log.ILoggable;

import net.minecraft.item.ItemStack;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fluids.FluidStack;

public interface ICultureRegistry extends ILoggable
{
	/**
	 * Adds the given ItemStack as a possible yeast item
	 *
	 * @param yeast - an item
	 */
	void addCulture(@Nonnull ItemStack yeast);

	/**
	 * @param yeast - an item
	 * @return true, if the item is a known yeast item, false otherwise.
	 */
	boolean isCulture(@Nullable ItemStack yeast);

	/**
	 * Maps the given biome type to the yeast item, when a Culture Jar
	 * is placed into a biome of that type, it MAY produce the yeast item.
	 * NOTE. This method SHOULD use addCulture to add the given yeast item to the
	 *       known list.
	 *
	 * @param yeast - an item
	 * @param type - the biome type to add
	 */
	void addCultureToBiomeType(@Nonnull ItemStack yeast, @Nonnull BiomeDictionary.Type type);

	/**
	 * Maps the given biome name to the yeast item.
	 * When a Culture Jar is placed in the Biome with the SAME name, it
	 * MAY produce the yeast item.
	 *
	 * @param yeast - an item
	 * @param name - biome name
	 */
	void addCultureToBiomeByName(@Nonnull ItemStack yeast, @Nonnull String name);

	void addCultureRecipeToBiomeType(@Nonnull ICultureRecipe recipe, @Nonnull BiomeDictionary.Type type);
	void addCultureRecipeToBiomeByName(@Nonnull ICultureRecipe recipe, @Nonnull String name);
	List<ICultureRecipe> getCultureRecipesForBiomeType(@Nonnull BiomeDictionary.Type type, @Nullable FluidStack stack);
	List<ICultureRecipe> getCultureRecipesForBiomeByName(@Nonnull String name, @Nullable FluidStack stack);

	/**
	 * Returns a Set of Items that may appear in this biome type
	 *
	 * @param type - the biome type
	 * @return yeast for the biome type,
	 */
	Set<ItemStack> getCultureListForBiomeType(@Nonnull BiomeDictionary.Type type);
	Set<ItemStack> getCultureListForBiomeName(@Nonnull String name);

	Set<String> getBiomeNamesForCulture(@Nullable ItemStack yeast);
	Set<BiomeDictionary.Type> getBiomeTypesForCulture(@Nullable ItemStack yeast);
	boolean canCultureFormInBiome(@Nullable ItemStack yeast, @Nullable BiomeGenBase biome);
}
