package games.chess;

import java.util.ArrayList;
import java.util.List;

public class chessCore
{
	// TYPES AND CLASSES
	private enum pieceType{pawn, bishop, knight, rook, queen, king};
	private enum objectColour{black, white};
	private enum pieceState{alive, dead};
	private enum gameState{allClear, whiteCheck, whiteMate, blackCheck, blackMate, stalemate};
	private enum moveStatus{success, fail, promote};

	private class player
	{
		// properties
		private objectColour colour;
		public objectColour getColour()
		{
			return colour;
		}
		
		private piece[] pieces;
		public piece[] getPieces()
		{
			return pieces;
		}
		
		//methods
		
		// move: moves the piece if the move is valid; returns false otherwise
		public boolean move(piece piece, cell moveTo) throws Exception
		{
			if(piece == null || piece.colour != this.colour || moveTo == null || moveTo == deadCell)
				return false;
			// TODO: cache this so that we're not constantly re-populating
			ArrayList<cell> availableMoves = piece.getAvailableMoves();
			if(availableMoves.contains(moveTo))
			{
				moveStatus status = piece.tryMove(moveTo);
				if(status != moveStatus.promote)
				{
					if(status == moveStatus.success)
					{
						return true;
					}
					else
						return false;
				}
				else
				{
					// TODO: decide: do we want to explode on incorrect promotion, or do we want
					// to eat the exception and loop until a valid selection is made.
					// For now, it'll explode under the assumption that the input is correct and
					// the code isn't complex enough to really have surprising results. 
					try
					{
						pieceType type = pieceType.bishop;
						// TODO: promotion logic
						// promotion code: UI, etc.
						piece.setPieceType(type);
						return true;
					}
					catch (incompatiblePieceTypeConversionException ex)
					{
						throw new Exception("MAN, WHAT THE FUCK!? Promotion code is seriously jacked up.");
					}
				}
			}
			return false;
		}
		
		// .ctor
		player(objectColour colour, piece[] pieces)
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
		
		private piece piece;
		public piece getPiece()
		{	return piece;}
		// if this replaces an existing piece, its location will be set to dead cell
		// under the assumption that it has been taken
		// if this is a cleanup call, pass in null
		// NOTE: deadCell never has a piece associated with it. 
		public void setPiece(piece piece)
		{
			if(piece != null)
				this.piece.setPieceState(pieceState.dead);
			this.piece = piece;
		}
		
		cell(int x, int y)
		{
			this.x = x;
			this.y = y;
			this.piece = null;
		}
	}
	
	public class incompatiblePieceTypeConversionException extends Exception
	{
		public incompatiblePieceTypeConversionException(String string) {
			super(string);
		}

		private static final long serialVersionUID = 1L;
	}
	
	private class piece
	{
		
		// properties
		private cell location;
		public cell getLocation()
		{	return location;}
		
		// state of the piece 		
		private pieceState state;
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
		public void setPieceType(pieceType newType) throws incompatiblePieceTypeConversionException
		{
			if(type != pieceType.pawn)
				throw new incompatiblePieceTypeConversionException("Error: can only convert pawns");
			else
			{
				switch (newType)
				{
				case bishop:
					availMoves = new bishop();
					break;
				case knight:
					availMoves = new knight();
					break;
				case rook:
					availMoves = new rook();
					break;
				case queen:
					availMoves = new queen();
					break;
				case pawn:
				case king:
					throw new incompatiblePieceTypeConversionException("Error: invalid conversion from pawn.");
				}
				
			}
		}
		
		// methods
		
		public boolean isValidMove(cell moveTo)
		{
			// clone board, try move
			// clone board
			cell tempBoard[][] = new cell[8][8];
			for(int i = 0; i < 8 ; i++)
			{
				for(int j = 0; i < 8 ; j++)
				{
					tempBoard[i][j] = new cell(i, j);
					tempBoard[i][j].setPiece(board[i][j].piece);
				}
			}
			// try move
			int oldX = location.x;
			int oldY = location.y;
			tempBoard[moveTo.x][moveTo.y].setPiece(this);
			this.location = tempBoard[moveTo.x][moveTo.y];
			tempBoard[oldX][oldY] = null;
			gameState tryState = checkForCheck(tempBoard, this.colour);
			
			// see if move is valid
			// validity of the move is determined by whether the move puts
			// the player into check. We know that the pieces cannot move
			// in invalid patterns because this has already been checked in
			// the only caller, which should be player.move()
			if((colour == objectColour.white && tryState == gameState.whiteCheck) ||
					(colour == objectColour.black && tryState == gameState.blackCheck))
				return false;
			return true;
		}
		
		// move takes a valid cell on the board and tries to move the
		// piece to it. if the move is successful, returns true, if not
		// returns false
		public moveStatus tryMove(cell moveTo)
		{
			
			if(!isValidMove(moveTo))
				return moveStatus.fail;
			// move is valid; apply move and check for promotion if pawn
			// empty old location
			this.location.setPiece(null);
			// update new location
			moveTo.setPiece(this);
			// update self location
			this.location = moveTo;
			
			if(type == pieceType.pawn)
			{
				return (location.y == 0 || location.y == 7) ? moveStatus.success: moveStatus.promote;
			}
			
			return moveStatus.success;
		}
		
		// gets a list of moves that don't go off the board or cause friendly fire
		private availableMoves availMoves;
		public ArrayList<cell> getAvailableMoves()
		{
			return availMoves.getAvailableMoves(this);
		}
		
		// .ctor
		piece(objectColour colour, pieceType type, cell location, availableMoves movementPattern)
		{
			this.colour = colour;
			this.type = type;
			this.location = location;
			this.availMoves = movementPattern;
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
			if(piece.getLocation() == deadCell)
				return null;
			ArrayList<cell> retList = new ArrayList<cell>();
			int currX = piece.getLocation().getX();
			int currY = piece.getLocation().getY();
			
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
			if(piece.getLocation() == deadCell)
				return null;
			ArrayList<cell> retList = new ArrayList<cell>();
			int currX = piece.getLocation().getX();
			int currY = piece.getLocation().getY();
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
			if(piece.getLocation() == deadCell)
				return null;
			ArrayList<cell> retList = new ArrayList<cell>();
			int currX = piece.getLocation().getX();
			int currY = piece.getLocation().getY();
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
			if(piece.getLocation() == deadCell)
				return null;
			ArrayList<cell> retList = new ArrayList<cell>();
			int currX = piece.getLocation().getX();
			int currY = piece.getLocation().getY();
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
			if(piece.getLocation() == deadCell)
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
			if(piece.getLocation() == deadCell)
				return null;
			ArrayList<cell> retList = new ArrayList<cell>();
			int currX = piece.getLocation().getX();
			int currY = piece.getLocation().getY();
			// if the king moves and there is a check, regardless of the colour
			// it is an invalid move.
			// TODO:implement king moves in a way that won't cause infinite loops
			// for example: checkForCheck doesn't look at the king. That would do it
			// Also: don't check for mate here. 
			
			if(currX > 0 && currY > 0 && piece.isValidMove(board[currX-1][currY-1]))
				retList.add(board[currX-1][currY-1]);
			
			if(currY > 0 && piece.isValidMove(board[currX][currY-1]))
				retList.add(board[currX][currY-1]);
			
			if(currX < 7 && currY > 0 && piece.isValidMove(board[currX+1][currY-1]))
				retList.add(board[currX+1][currY-1]);
			
			if(currX < 7 && piece.isValidMove(board[currX+1][currY]))
				retList.add(board[currX+1][currY]);
			
			if(currX < 7 && currY < 7 && piece.isValidMove(board[currX+1][currY+1]))
				retList.add(board[currX+1][currY+1]);
			
			if(currY < 7 && piece.isValidMove(board[currX][currY+1]))
				retList.add(board[currX][currY+1]);
			
			if(currX > 0 && currY > 7 && piece.isValidMove(board[currX-1][currY+1]))
				retList.add(board[currX-1][currY+1]);
			
			if(currX > 0 && piece.isValidMove(board[currX-1][currY]))
				retList.add(board[currX-1][currY]);
			
			return retList;
		}
	}
	
	private class userInterfaceBoard
	{
		public cell getTargetCell()
		{
			// TODO: write UI interaction
			cell retCell = deadCell;
			return retCell;
		}
		
		public cell getSourceCell()
		{
			// TODO: write UI interaction
			cell retCell = deadCell;
			return retCell;
		}
		
		public piece getPiece()
		{
			// TODO: i'm not sure that this should really come from the source cell. 
			// we'll see
			piece retPiece = getSourceCell().getPiece();
			return retPiece;
		}
		
		// .ctor
		userInterfaceBoard()
		{}
	}
	// MEMBER VARIABLES
	
	// this gets used for discarded pieces
	private cell deadCell;
	private cell board[][];
	private player white, black;
	private objectColour turn;
	private gameState currentGameState;
	private userInterfaceBoard UIboard;
	
	// METHODS
	
	// checks for check on proposed board.
	private gameState checkForCheck(cell board[][], objectColour whoseCheck)
	{
		piece own[];
		piece opponent[];
		if(whoseCheck == objectColour.white)
		{
			own = white.getPieces();
			opponent = black.getPieces();
		}
		else
		{
			own = black.getPieces();
			opponent = white.getPieces();
		}
		// i < 16 because we're not checking the king for move possibilities
		// as that would cause an infinite loop since the king checks for
		// checks in available moves;
		for(int i = 0; i < 16; i++)
		{
			if(opponent[i].getAvailableMoves().contains(own[16].getLocation()))
			{
				if(whoseCheck == objectColour.white)
					return gameState.whiteCheck;
				else
					return gameState.blackCheck;
			}
		}
		return gameState.allClear;
	}
	
	// checks for mate on the board as it exists
	// TODO: finish this. Not important at the moment. 
	private gameState checkForMate()
	{
		// TODO: check for stalemate here
		// mate = check & no valid moves as defined above(somewhere)
		// stalemate = no available moves and no check
		if(checkForCheck(board, objectColour.white) == gameState.whiteCheck)
			return gameState.whiteCheck;
		if(checkForCheck(board, objectColour.black) == gameState.blackCheck)
			return gameState.blackCheck;
		return gameState.allClear;
	}
	
	// TODO: maybe move this into a game class
	void move() throws Exception
	{
		player currentPlayer = turn == objectColour.white ? white : black;
		boolean success = false;
		while(!success)
		{
			// TODO: write UI interaction
			cell moveTo = UIboard.getTargetCell();
			piece piece = UIboard.getPiece();
			success = currentPlayer.move(piece, moveTo);
		}
		
	}
	
	// TODO: finish this function. 
	void populateBoard(cell[][] board)
	{
		// create player piece arrays
		// first 8 cells are pawns, then rooks, knight, bishops, queen, king
		piece blackPieces[] = new piece[16];
		piece whitePieces[] = new piece[16];
		
		// pawns
		for(int i = 0; i < 8; i ++)
		{
			board[i][1] = new cell(i, 1);
			piece whitePawn = new piece(objectColour.white, pieceType.pawn, board[i][1], new pawn());
			whitePieces[i] = whitePawn;
			board[i][6] = new cell(i, 6);
			piece blackPawn = new piece(objectColour.black, pieceType.pawn, board[i][6], new pawn());
			blackPieces[i] = blackPawn;
		}
	}
	
	// .ctor
	chessCore()
	{
		// if new game
		deadCell = new cell(-1, -1);
		board = new cell[8][8];
		populateBoard(board);
		turn = objectColour.white;
		currentGameState = gameState.allClear;
	}
}
