package eyeq.clock.block;

import eyeq.util.world.WorldTimeUtils;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public class BlockClock extends BlockHorizontal {
    public static final PropertyInteger TIME = PropertyInteger.create("time", 1, 12);
    public static final AxisAlignedBB CLOCK_AABB = new AxisAlignedBB(0.3, 0.05, 0.25, 0.7, 0.55, 0.75);

    public BlockClock(Material material) {
        super(material);
        this.setDefaultState(this.blockState.getBaseState().withProperty(TIME, 1));
        this.setTickRandomly(true);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return CLOCK_AABB;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World world, BlockPos pos) {
        return CLOCK_AABB.offset(pos);
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        super.updateTick(world, pos, state, rand);
        if(!world.isRemote) {
            int now = WorldTimeUtils.getHour(world) % 12;
            if(now == 0) {
                now = 12;
            }
            if(now != state.getValue(TIME)) {
                world.setBlockState(pos, state.withProperty(TIME, now), 2);
                world.playSound(null, pos, SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.BLOCKS, 0.3F, now / 16);
                world.notifyNeighborsOfStateChange(pos, this, false);
            }
        }
        world.scheduleUpdate(pos, this, this.tickRate(world));
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        super.onBlockAdded(world, pos, state);
        world.scheduleUpdate(pos, this, 1);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        world.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()), 2);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if(world.isRemote) {
            return true;
        }
        world.setWorldTime(world.getWorldTime() + WorldTimeUtils.SECONDS_IN_HOUR);
        return true;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        EnumFacing facing = EnumFacing.getFront(meta);
        if(facing.getAxis() == EnumFacing.Axis.Y) {
            facing = EnumFacing.NORTH;
        }
        return this.getDefaultState().withProperty(FACING, facing);
    }

    @Override
    public int getWeakPower(IBlockState state, IBlockAccess access, BlockPos pos, EnumFacing facing) {
        return state.getValue(TIME);
    }

    @Override
    public int getStrongPower(IBlockState state, IBlockAccess access, BlockPos pos, EnumFacing facing) {
        return state.getValue(TIME);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getIndex();
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING, TIME);
    }
}
