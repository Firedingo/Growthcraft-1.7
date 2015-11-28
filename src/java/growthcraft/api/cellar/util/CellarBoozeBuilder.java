/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 IceDragon200
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
package growthcraft.api.cellar.util;

import javax.annotation.Nonnull;

import growthcraft.api.cellar.booze.BoozeEffect;
import growthcraft.api.cellar.booze.BoozeTag;
import growthcraft.api.cellar.CellarRegistry;
import growthcraft.api.cellar.common.Residue;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

/**
 * If you find yourself making some seriously gnarly spaghetti code, this may
 * save you.
 */
public class CellarBoozeBuilder
{
	private Fluid fluid;

	public CellarBoozeBuilder(Fluid f)
	{
		this.fluid = f;
	}

	public CellarBoozeBuilder tags(BoozeTag... tags)
	{
		CellarRegistry.instance().booze().addTags(fluid, tags);
		return this;
	}

	public CellarBoozeBuilder brewsTo(@Nonnull FluidStack result, @Nonnull ItemStack stack, int time, @Nonnull Residue residue)
	{
		CellarRegistry.instance().brewing().addBrewing(new FluidStack(fluid, result.amount), stack, result, time, residue);
		return this;
	}

	public CellarBoozeBuilder brewsFrom(@Nonnull FluidStack src, @Nonnull ItemStack stack, int time, @Nonnull Residue residue)
	{
		CellarRegistry.instance().brewing().addBrewing(src, stack, new FluidStack(fluid, src.amount), time, residue);
		return this;
	}

	public CellarBoozeBuilder fermentsTo(@Nonnull FluidStack result, @Nonnull ItemStack stack, int time)
	{
		CellarRegistry.instance().fermenting().addFermentingRecipe(result, new FluidStack(fluid, result.amount), stack, time);
		return this;
	}

	public CellarBoozeBuilder fermentsFrom(@Nonnull FluidStack src, @Nonnull ItemStack stack, int time)
	{
		CellarRegistry.instance().fermenting().addFermentingRecipe(new FluidStack(fluid, src.amount), src, stack, time);
		return this;
	}

	public CellarBoozeBuilder pressesFrom(@Nonnull ItemStack stack, int time, int amount, @Nonnull Residue residue)
	{
		CellarRegistry.instance().pressing().addPressingRecipe(stack, new FluidStack(fluid, amount), time, residue);
		return this;
	}

	public BoozeEffect getEffect()
	{
		return CellarRegistry.instance().booze().getEffect(fluid);
	}

	public static CellarBoozeBuilder create(Fluid f)
	{
		return new CellarBoozeBuilder(f);
	}
}