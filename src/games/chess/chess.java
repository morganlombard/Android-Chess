package games.chess;

import android.app.Activity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class chess extends Activity {

	// TYPES AND CLASSES
	private enum pieceType{pawn, bishop, knight, rook, queen, king};
	private enum objectColour{black, white};
	private enum pieceState{alive, dead};
	private enum gameState{allClear, whiteCheck, whiteMate, blackCheck, blackMate, stalemate};

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
		private availableMoves availMoves;
		public ArrayList<cell> getAvailableMoves()
		{
			return availMoves.getAvailableMoves(this);
		}
		
		
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
	
	private interface availableMoves
	{
		public ArrayList<cell>getAvailableMoves(piece piece); 
	}

	private class pawn implements availableMoves
	{
		// only piece whose moves depend on colour
		public ArrayList<cell>getAvailableMoves(piece piece)
		{
			if(piece.location == deadCell)
				return null;
			ArrayList<cell> retList = new ArrayList<cell>();
			int currX = piece.location.getX();
			int currY = piece.location.getY();
			
			// this could be better implemented with some sort of
			// direction boolean deciding addition + subtraction, but 
			// i don't feel like dicking with it, so here are two
			// separate blocks of code. 
			
			// as standard, white is on bottom, black on top
			// white moves 6 -> 0; black moves 1 -> 7 
			if(piece.colour == objectColour.white)
			{
				// move forward one cell
				if(board[currX][currY-1].piece == null)
					retList.add(board[currX][currY-1]);
					
				// check moving left
				if(currX > 0 && 
						board[currX-1][currY-1].piece != null && 
						board[currX-1][currY-1].piece.colour == objectColour.black)
					retList.add(board[currX-1][currY-1]);
				
				// check moving right
				if(currX < 7 && 
						board[currX+1][currY-1].piece != null && 
						board[currX+1][currY-1].piece.colour == objectColour.black)
					retList.add(board[currX+1][currY-1]);

				// default location, making 4 available moves, rather than 3
				if(currY == 6 && board[currX][currY-1].piece == null &&
						board[currX][currY-2].piece == null)
					retList.add(board[currX][currY-2]);
			}
			if(piece.colour == objectColour.black)
			{
				// COPY PASTA! YAAY! : (
				// move forward one cell
				if(board[currX][currY+1].piece == null)
					retList.add(board[currX][currY-1]);
					
				// check moving left
				if(currX > 0 && 
						board[currX-1][currY+1].piece != null && 
						board[currX-1][currY+1].piece.colour == objectColour.black)
					retList.add(board[currX-1][currY+1]);
				
				// check moving right
				if(currX < 7 && 
						board[currX+1][currY+1].piece != null && 
						board[currX+1][currY+1].piece.colour == objectColour.black)
					retList.add(board[currX+1][currY+1]);

				// default location, making 4 available moves, rather than 3
				if(currY == 6 && board[currX][currY+1].piece == null &&
						board[currX][currY+2].piece == null)
					retList.add(board[currX][currY+2]);
			}
			return retList;
		}
	}

	private class bishop implements availableMoves
	{
		public ArrayList<cell>getAvailableMoves(piece piece)
		{
			if(piece.location == deadCell)
				return null;
			ArrayList<cell> retList = new ArrayList<cell>();
			int currX = piece.location.getX();
			int currY = piece.location.getY();
			// set i to 1 because there's no point in checking whether
			// staying in place is a valid move. that's fucking stupid
			// even with colour checking
			int i = 1;
			
			// check the diagonal until you see a piece.
			// right, down
			while(currX + i < 8 && currY + 1 < 8 && board[currX + i][currY + i].piece == null ||
					board[currX + i][currY + i].piece.colour != piece.colour)
			{
				// if the piece is one of the opponent's, it is a valid move
				if(board[currX + i][currY + i].piece != null && 
						board[currX + i][currY + i].piece.colour != piece.colour)
				{
					retList.add(board[currX + i][currY + i]);
					break;
				}
				retList.add(board[currX+i][currY+i]);
				i++;
			}
			
			i = 1;
			// right, up
			while(currX + i < 8 && currY - 1 > -1 && board[currX + i][currY - i].piece == null ||
					board[currX + i][currY- i].piece.colour != piece.colour)
			{
				if(board[currX + i][currY - i].piece != null && 
						board[currX + i][currY- i].piece.colour != piece.colour)
				{
					retList.add(board[currX + i][currY - i]);
					break;
				}
				retList.add(board[currX+i][currY-i]);
				i++;
			}
			
			i = 1;
			// left, down
			while(currX - i > -1 && currY + 1 < 8 && board[currX - i][currY + i].piece == null || 
					board[currX - i][currY + i].piece.colour != piece.colour)
			{
				if(board[currX - i][currY + i].piece != null && 
						board[currX - i][currY + i].piece.colour != piece.colour)
				{
					retList.add(board[currX - i][currY + i]);
					break;
				}
				retList.add(board[currX-i][currY+i]);
				i++;
			}
			
			
			i = 1;
			// left, up
			while(currX - i > -1 && currY - 1 > -1 && board[currX - i][currY - i].piece == null ||
					board[currX - i][currY - i].piece.colour != piece.colour)
			{
				if(board[currX - i][currY - i].piece != null && 
						board[currX - i][currY - i].piece.colour != piece.colour)
				{
					retList.add(board[currX - i][currY - i]);
					break;
				}
				retList.add(board[currX-i][currY-i]);
				i++;
			}
			
			return retList;
		}
	}

	private class knight implements availableMoves
	{
		public ArrayList<cell>getAvailableMoves(piece piece)
		{
			if(piece.location == deadCell)
				return null;
			ArrayList<cell> retList = new ArrayList<cell>();
			int currX = piece.location.getX();
			int currY = piece.location.getY();
			// the knight has 8 moves, so we'll just explicitly define them
			// since it's not that much more verbose than the alternateive
			// and much easier to write.
			// TODO: think about optimizing this. not a priority at the moment
			// like, this is a circular pattern, or check the rectangle 2 away,
			// skipping every other cell
			
			if(currX > 1 && currY > 0 && board[currX-2][currY-1].piece == null ||
					board[currX-2][currY-1].piece.colour != piece.colour)
				retList.add(board[currX-2][currY-1]);
			
			if(currX > 0 && currY > 1 && board[currX-1][currY-2].piece == null ||
					board[currX-1][currY-2].piece.colour != piece.colour)
				retList.add(board[currX-1][currY-2]);
			
			if(currX < 7 && currY > 1  && board[currX+1][currY-2].piece == null ||
					board[currX+1][currY-2].piece.colour != piece.colour)
				retList.add(board[currX+1][currY-2]);
			
			if(currX < 6  && currY > 0 && board[currX+2][currY-1].piece == null ||
					board[currX+2][currY-1].piece.colour != piece.colour)
				retList.add(board[currX+2][currY-1]);
			
			if(currX < 6 && currY < 7 && board[currX+2][currY+1].piece == null ||
					board[currX+2][currY+1].piece.colour != piece.colour)
				retList.add(board[currX+2][currY+1]);
			
			if(currX < 7 && currY < 6 && board[currX+1][currY+2].piece == null ||
					board[currX+1][currY+2].piece.colour != piece.colour)
				retList.add(board[currX+1][currY+2]);
			
			if(currX > 0 && currY < 6 && board[currX-1][currY+2].piece == null ||
					board[currX-1][currY+2].piece.colour != piece.colour)
				retList.add(board[currX-1][currY+2]);
			
			if(currX > 1 && currY < 7 && board[currX-2][currY+1].piece == null ||
					board[currX-2][currY+1].piece.colour != piece.colour)
				retList.add(board[currX-2][currY+1]);
			
			return retList;
		}
	}

	private class rook implements availableMoves
	{
		public ArrayList<cell>getAvailableMoves(piece piece)
		{
			if(piece.location == deadCell)
				return null;
			ArrayList<cell> retList = new ArrayList<cell>();
			int currX = piece.location.getX();
			int currY = piece.location.getY();
			int i = 1;
			
			// right
			while(currX + i < 8 && (board[currX + i][currY].piece == null ||
					board[currX + i][currY].piece.colour != piece.colour))
			{
				if(board[currX + i][currY].piece != null && 
						board[currX + i][currY].piece.colour != piece.colour)
				{
					retList.add(board[currX + i][currY]);
					break;
				}
				retList.add(board[currX+i][currY]);
				i++;
			}
			
			i = 1;
			// left
			while(currX - i > -1 && (board[currX - i][currY].piece == null || 
					board[currX - i][currY].piece.colour != piece.colour))
			{
				if(board[currX - i][currY].piece != null && 
						board[currX - i][currY].piece.colour != piece.colour)
				{
					retList.add(board[currX - i][currY]);
					break;
				}
				retList.add(board[currX-i][currY]);
				i++;
			}
			
			i = 1;
			// down
			while(currY + i < 8 && (board[currX][currY + i].piece == null || 
					board[currX][currY + i].piece.colour != piece.colour))
			{
				if(board[currX][currY + i].piece != null && 
						board[currX][currY + i].piece.colour != piece.colour)
				{
					retList.add(board[currX][currY + i]);
					break;
				}
				retList.add(board[currX][currY+i]);
				i++;
			}	
			
			i = 1;
			// up
			while(currY - i > -1 && (board[currX][currY - i].piece == null || 
					board[currX][currY - i].piece.colour != piece.colour))
			{
				if(board[currX][currY - i].piece != null && 
						board[currX][currY -i ].piece.colour != piece.colour)
				{
					retList.add(board[currX][currY - i]);
					break;
				}
				retList.add(board[currX][currY-i]);
				i++;
			}
			return retList;
		}
	}

	private class queen implements availableMoves
	{
		private rook horizontalVerical;
		private bishop diagonal;
		public ArrayList<cell>getAvailableMoves(piece piece)
		{
			if(piece.location == deadCell)
				return null;
			ArrayList<cell> retList = horizontalVerical.getAvailableMoves(piece);
			ArrayList<cell> moreMoves = diagonal.getAvailableMoves(piece);
			for(int i = 0; i < moreMoves.size(); i++)
			{
				retList.add(moreMoves.get(i));
			}
			return retList;
		}
	}
	
	private class king implements availableMoves
	{
		public ArrayList<cell>getAvailableMoves(piece piece)
		{
			if(piece.location == deadCell)
				return null;
			ArrayList<cell> retList = new ArrayList<cell>();
			int currX = piece.location.getX();
			int currY = piece.location.getY();
			// if the king moves and there is a check, regardless of the colour
			// it is an invalid move.
			// TODO:implement king moves in a way that won't cause infinite loops
			// for example: checkForCheck doesn't look at the king. That would do it
			
			
			return retList;
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
	{
		return gameState.allClear;
	}
	
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