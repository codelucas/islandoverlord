package com.lukepop.island;

public class Expense 
{
	public int[] resourceCost = new int[4];
	public int[] supernatCost = new int[1];
	
	public Expense(int woodCost, int mineralCost, int foodCost, int intelligence)
	{
		resourceCost[0] = woodCost;
		resourceCost[1] = mineralCost;
		resourceCost[2] = foodCost;
		resourceCost[3] = intelligence;
	}
	
	public Expense(int godEnergy)
	{
		supernatCost[0] = godEnergy;
	}
	
	public int[] returnResourceCost()
	{
		return resourceCost;
	}
	
	public int[] returnSupernatCost()
	{
		return supernatCost;
	}
}
