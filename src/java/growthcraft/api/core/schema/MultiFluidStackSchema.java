/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 IceDragon200
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
package growthcraft.api.core.schema;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nonnull;

import growthcraft.api.core.definition.IFluidStackFactory;
import growthcraft.api.core.util.FluidUtils;
import growthcraft.api.core.CoreRegistry;
import growthcraft.api.core.fluids.IFluidTagsRegistry;
import growthcraft.api.core.fluids.FluidTag;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidRegistry;

public class MultiFluidStackSchema implements ICommentable, IValidatable
{
	public List<String> names = new ArrayList<String>();
	public List<String> inclusion_tags = new ArrayList<String>();
	public List<String> exclusion_tags = new ArrayList<String>();
	public String comment = "";
	public int amount;

	@Override
	public void setComment(String comm)
	{
		this.comment = comm;
	}

	@Override
	public String getComment()
	{
		return comment;
	}

	private List<FluidTag> expandTagNames(List<String> tagNames)
	{
		final IFluidTagsRegistry fluidTags = CoreRegistry.instance().fluidTags();
		final List<FluidTag> tags = new ArrayList<FluidTag>();
		for (String name : tagNames)
		{
			final FluidTag tag = fluidTags.findTag(name);
			if (tag != null)
			{
				tags.add(tag);
			}
			else
			{
				// WARN user
			}
		}
		return tags;
	}

	public List<FluidTag> expandInclusionTags()
	{
		return expandTagNames(inclusion_tags);
	}

	public List<FluidTag> expandExclusionTags()
	{
		return expandTagNames(exclusion_tags);
	}

	public List<Fluid> getFluids()
	{
		final List<Fluid> result = new ArrayList<Fluid>();
		final Collection<Fluid> fluids = CoreRegistry.instance().fluidDictionary().getFluidsByTags(expandInclusionTags());
		final Collection<Fluid> exfluids = CoreRegistry.instance().fluidDictionary().getFluidsByTags(expandExclusionTags());
		result.addAll(fluids);
		result.removeAll(exfluids);
		for (String name : names)
		{
			final Fluid fluid = FluidRegistry.getFluid(name);
			if (fluid != null)
			{
				result.add(fluid);
			}
		}
		return result;
	}

	public List<FluidStack> getFluidStacks()
	{
		final List<FluidStack> stacks = new ArrayList<FluidStack>();
		for (Fluid fluid : getFluids())
		{
			stacks.add(new FluidStack(fluid, amount));
		}
		return stacks;
	}

	public static MultiFluidStackSchema newWithTags(int amount, String... tags)
	{
		final MultiFluidStackSchema schema = new MultiFluidStackSchema();
		for (String tag : tags)
		{
			schema.inclusion_tags.add(tag);
		}
		schema.amount = amount;
		return schema;
	}

	@Override
	public boolean isValid()
	{
		return !getFluids().isEmpty();
	}

	@Override
	public boolean isInvalid()
	{
		return !isValid();
	}

	@Override
	public String toString()
	{
		return String.format("Schema<MultiFluidStack>(names: [%s], inclusion_tags: [%s], exclusion_tags: [%s], amount: %d)", names, inclusion_tags, exclusion_tags, amount);
	}
}
