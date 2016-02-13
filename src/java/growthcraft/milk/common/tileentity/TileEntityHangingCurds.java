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
package growthcraft.milk.common.tileentity;

import java.io.IOException;

import growthcraft.api.core.util.PulseStepper;
import growthcraft.api.core.util.SpatialRandom;
import growthcraft.api.core.util.Pair;
import growthcraft.api.core.util.TickUtils;
import growthcraft.api.core.util.FXHelper;
import growthcraft.core.common.tileentity.GrcTileEntityBase;
import growthcraft.milk.GrowthCraftMilk;
import growthcraft.core.common.tileentity.event.EventHandler;

import io.netty.buffer.ByteBuf;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraft.nbt.NBTTagCompound;

public class TileEntityHangingCurds extends GrcTileEntityBase
{
	private SpatialRandom sprand = new SpatialRandom();
	private PulseStepper pulsar = new PulseStepper(TickUtils.seconds(15), 10);

	// the following variables are responsible for step tracking
	@SideOnly(Side.CLIENT)
	// This pulse stepper is used to control the 'drip' animation
	private PulseStepper animPulsar = new PulseStepper(10, 4);

	// The server will increment this value whenever it does a drip step
	private int serverStep;

	@SideOnly(Side.CLIENT)
	// Clients will set this value to the serverStep value and proceed with the drip animation
	private int clientStep;

	private IPancheonTile getPancheonTile()
	{
		final TileEntity te = worldObj.getTileEntity(xCoord, yCoord - 1, zCoord);
		if (te instanceof IPancheonTile)
		{
			return (IPancheonTile)te;
		}
		return null;
	}

	@Override
	public void updateEntity()
	{
		super.updateEntity();

		if (!worldObj.isRemote)
		{
			if (pulsar.update() == PulseStepper.State.PULSE)
			{
				final IPancheonTile pancheonTile = getPancheonTile();
				// I a pancheon is present, try filling it with Whey
				if (pancheonTile != null)
				{
					final FluidStack stack = GrowthCraftMilk.fluids.whey.fluid.asFluidStack(100);
					if (pancheonTile.canFill(ForgeDirection.UP, stack.getFluid()))
					{
						pancheonTile.fill(ForgeDirection.UP, stack, true);
					}
				}
				// regardless of a pancheon being present, the curd SHOULD drip
				serverStep++;
				markForBlockUpdate();
			}
		}
		else
		{
			if (clientStep != serverStep)
			{
				this.clientStep = serverStep;
				animPulsar.reset();
			}

			if (animPulsar.update() == PulseStepper.State.PULSE)
			{
				final Pair<Double, Double> p = sprand.nextCenteredD2();
				final double px = xCoord + 0.5 + p.left * 0.5;
				final double py = yCoord;
				final double pz = zCoord + 0.5 + p.right * 0.5;
				FXHelper.dropParticle(worldObj, px, py, pz, GrowthCraftMilk.fluids.whey.getItemColor());
			}
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		pulsar.readFromNBT(nbt, "pulsar");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		pulsar.writeToNBT(nbt, "pulsar");
	}

	@EventHandler(type=EventHandler.EventType.NETWORK_READ)
	public boolean readFromStream_HangingCurds(ByteBuf stream) throws IOException
	{
		pulsar.readFromStream(stream);
		this.serverStep = stream.readInt();
		return true;
	}

	@EventHandler(type=EventHandler.EventType.NETWORK_WRITE)
	public void writeToStream_HangingCurds(ByteBuf stream) throws IOException
	{
		pulsar.writeToStream(stream);
		stream.writeInt(serverStep);
	}
}
