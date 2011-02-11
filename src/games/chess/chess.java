package games.chess;

import android.app.Activity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class chess extends Activity {

	// TYPES AND CLASSES
	private enum pieceType{pawn, bishop, knight, rook, queen, king};
	private enum objectColour{black, white};
	private enum pieceState{alive, dead, tentativelyDead};
	private enum gameState{allClear, whiteCheck, whiteMate, blackCheck, blackMate};

	private class user
	{
		private objectColour colour;
		private piece[] pieces;
		
		// .ctor
		user(objectColour colour, piece[] pieces)
		{
			this.colour = colour;
			this.pieces = pieces;
		}
	}
	
	private class cell
	{
		private int x;
		public int getX()
		{	return x; }
		
		private int y;
		public int getY()
		{	return y; }
		
		public piece piece;
		
		cell(int x, int y)
		{
			this.x = x;
			this.y = y;
		}
	}
	
	private abstract class piece
	{
		protected cell location;
		// properties
		// state of the piece 
		protected pieceState state;
		public pieceState getPieceState()
		{	return state;}
		public void setPieceState(pieceState newState)
		{
			state = newState;
			if (newState == pieceState.dead)
			{
				location.piece = null;
				location = deadCell;
			}
		}
		
		// colour of the piece
		protected objectColour colour;
		public objectColour getPieceColour()
		{	return colour;}
		
		// type of piece 
		private pieceType type;
		public pieceType getPieceType()
		{	return type;}
		
		// methods
		// move takes a valid cell on the board and tries to move the
		// piece to it. if the move is successful, returns true, if not
		// returns false
		abstract protected boolean move(cell moveTo);
		
		// gets a list of moves that don't go off the board or cause friendly fire
		abstract public List<cell> getAvailableMoves();
		
		// TODO: this is seriously complicated shit. Leave this alone for now
		protected boolean validateMove(cell moveTo)
		{
			if (currentGameState != gameState.allClear && currentGameState == checkForCheck())
			{
				if((colour == objectColour.white && currentGameState == gameState.whiteCheck ) || 
						(colour == objectColour.black && currentGameState == gameState.blackCheck))
				// reset to previous state
				return false;
			}
			return true;
		}
		
		// .ctor
		piece(objectColour colour, pieceType type, cell location)
		{
			this.colour = colour;
			this.type = type;
			this.location = location;
		}
		
	}
	
	private class pawn extends piece
	{
		@Override 
		public boolean move(cell moveTo)
		{
			// TODO: handle board edge reached case. Probably make a call to super, 
			// either causing a re-cast, or just replace self in the cell
			return true;
		}
		
		// only piece whose moves depend on colour
		@Override
		public List<cell>getAvailableMoves()
		{
			List<cell> retList = new ArrayList<cell>();
			int currX = location.getX();
			int currY = location.getY();
			
			// this could be better implemented with some sort of
			// direction boolean deciding addition + subtraction, but 
			// i don't feel like dicking with it, so here are two
			// separate blocks of code. 
			
			// as standard, white is on bottom, black on top
			// white moves 6 -> 0; black moves 1 -> 7 
			if(this.colour == objectColour.white)
			{
				// directly blocked by piece in front; check diagonal movement
				if(board[currX][currY-1].piece != null)
				{
					// check moving left
					if(currX > 0 && 
							board[currX-1][currY-1].piece != null && 
							board[currX-1][currY-1].piece.colour == objectColour.black)
					{
						
					}
				}
				// default location, making 4 available moves, rather than 3
				if(currY == 6)
				{
					
					
					
				}
			}
			if(this.colour == objectColour.black)
			{
				
			}
			return retList;
		}
		
		pawn(objectColour colour, cell location){
			super(colour, pieceType.pawn, location);
		}
	}

	private class bishop extends piece
	{
		@Override 
		public boolean move(cell moveTo)
		{
			return true;
		}
		
		@Override
		public List<cell>getAvailableMoves()
		{
			List<cell> retList = new ArrayList<cell>();
			return retList;
		}
		
		bishop(objectColour colour, cell location){
			super(colour, pieceType.bishop, location);
		}
	}

	private class knight extends piece
	{
		@Override 
		public boolean move(cell moveTo)
		{
			return true;
		}
		
		@Override
		public List<cell>getAvailableMoves()
		{
			List<cell> retList = new ArrayList<cell>();
			return retList;
		}
		
		knight(objectColour colour, cell location){
			super(colour, pieceType.knight, location);
		}
	}

	private class rook extends piece
	{
		@Override 
		public boolean move(cell moveTo)
		{
			return true;
		}
		
		@Override
		public List<cell>getAvailableMoves()
		{
			List<cell> retList = new ArrayList<cell>();
			return retList;
		}
		
		rook(objectColour colour, cell location){
			super(colour, pieceType.rook, location);
		}
	}

	private class queen extends piece
	{
		@Override 
		public boolean move(cell moveTo)
		{
			return true;
		}
		
		@Override
		public List<cell>getAvailableMoves()
		{
			List<cell> retList = new ArrayList<cell>();
			return retList;
		}
		
		queen(objectColour colour, cell location){
			super(colour, pieceType.queen, location);
		}
	}

	private class king extends piece
	{
		@Override 
		public boolean move(cell moveTo)
		{
			return true;
		}
		
		@Override
		public List<cell>getAvailableMoves()
		{
			List<cell> retList = new ArrayList<cell>();
			return retList;
		}
		
		king(objectColour colour, cell location){
			super(colour, pieceType.king, location);
		}
	}
	
	// MEMBER VARIABLES
	
	// this gets used for discarded pieces
	private cell deadCell;
	private cell board[][];
	private objectColour turn;
	private gameState currentGameState;
	
	// METHODS
	
	private gameState checkForCheck()
	{
		return gameState.allClear;
	}
	
	private gameState checkForMate() 
	
	// .ctor
	chess()
	{
		// if new game
		deadCell = new chess.cell(-1, -1);
		board = new cell[8][8];
		turn = objectColour.white;
		currentGameState = gameState.allClear;
	}
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
}