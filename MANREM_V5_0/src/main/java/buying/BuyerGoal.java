package buying;

import jadex.bdiv3.annotation.Goal;

@Goal
public class BuyerGoal {

	/**
	 * Preco desejado no mercado
	 */
	private long desiredPrice;
	
	/**
	 * Preco atual do no mercado
	 */
	private long currentPrice;
	
	/**
	 * Pretendo comprar energia, sim ou nao?
	 */
	private boolean buyMarketEnergy;

	/**
	 * Construtor com todos os parametros desejados
	 * 
	 * @param desiredPrice
	 * @param currentPrice
	 * @param buyMarketEnergy
	 */
	public BuyerGoal(long desiredPrice, long currentPrice, boolean buyMarketEnergy) {
		this.desiredPrice = desiredPrice;
		this.currentPrice = currentPrice;
		this.buyMarketEnergy = buyMarketEnergy;
	}
	
	/**
	 * Construtor com o preco desejado no mercado
	 * 
	 * @param desiredPrice
	 */
	public BuyerGoal(long desiredPrice) {
		this.desiredPrice = desiredPrice;
	}

	public long getDesiredPrice() {
		return desiredPrice;
	}

	public void setDesiredPrice(long desiredPrice) {
		this.desiredPrice = desiredPrice;
	}

	public long getCurrentPrice() {
		return currentPrice;
	}

	public void setCurrentPrice(long currentPrice) {
		this.currentPrice = currentPrice;
	}

	public boolean isBuyMarketEnergy() {
		return buyMarketEnergy;
	}

	public void setBuyMarketEnergy(boolean buyMarketEnergy) {
		this.buyMarketEnergy = buyMarketEnergy;
	}

	
	
}
