//convert text to a string
//convert string to an array of substring of length 200
//create plaintext blocks of BigIntegers of length 600
//Encrypt the 600 digit blocks
//decrypt the encrypted blocks
//unpad the ascii to get the 200 block strings
//output to a file
import java.math.*;
import java.util.*;
import java.io.*;

public class RSA{
	public static void main(String[] args){
		BigInteger p, q, n, phi_of_n, e, d, one;
		p=BigInteger.ONE;
		q=BigInteger.ONE;
		n=BigInteger.ONE;
		phi_of_n=BigInteger.ONE;
		e= BigInteger.ONE;
		d= BigInteger.ONE;
		one = BigInteger.ONE;
		
		String s = fileToString("./awmt.txt");
		//Turns the text into blocks of strings
		ArrayList<String> sBlocks = toStringBlocks(s);
		
		//Turns the string blocks into padded ascii values per character
		ArrayList<BigInteger> plainText = toBigIntBlocks(sBlocks);
		
		
		//Generates a p and q of length 300
		//boolean validPrimes=false;
		/*while (!validPrimes) {
			p = randomPrime(300);
			q = randomPrime(300);
			n = p.multiply(q);
			if (testLength(n)) {
				validPrimes = true;
			}
		}*/
		
		//Encryption values
		//p and q were previously generated
		p = new BigInteger("3362327510949069277068784884690918449394539504808263321788430077271677416931013117369850269412222256384749071065459306702751619930023594674393016835264405631481906230323611361394143348296585061174935105765653584098580467800741631535708893553561022682073748350336883374620504601626861443840177540197751");
		q = new BigInteger("281333344708466996369222542624189842205042006818321754789752441165074558579896197113794968264053774765459740024062484640941468064588529738912956332676988612044265834581230173365857230794208116776031207537198467051593714547703844957235121014710878472594892451536753038441077793343874062281175054427453");
		//e is given, has to be coprime to phi(n)
		e = new BigInteger("65537");
		n = p.multiply(q);
		
		//Decipher keys
		phi_of_n = phi(p,q);
		d = inverse(e, phi_of_n);
		//Encrypts padded BigInteger blocks
		ArrayList<BigInteger> cipherText = encrypt(plainText, e, n);
		
		//Deciphers encrypted padded BigInteger blocks
		ArrayList<BigInteger> decipherText = decrypt(cipherText, d, n);
		
		//Unpads the decrypted padded BigInteger blocks
		ArrayList<String> decipherPlainText = plainTextToStringBlocks(decipherText);
		
		//Saves the string blocks to the file result.txt
		stringToFile(decipherPlainText);
	}
	
	
	//return a random integer approx. ndigits in length (base 10).
	//We're converting from Java's base 2.
	public static BigInteger randomInteger(int ndigits){
		Random rand = new Random();
		int len = (int)(3.32*(double)ndigits);
		return new BigInteger(len, rand);
	}
	
	//return a random prime approx. n digits in length
	public static BigInteger randomPrime(int ndigits){
		BigInteger p = randomInteger(ndigits);
		return p.nextProbablePrime();
	}
	
	//Tests if the length of the product is bigger than the largest posible encrypted block
	public static boolean testLength(BigInteger n) {
		if (n.toString().length() == 600) {
			return true;
		}
		return false;
	}
	
	//Since p and q are both primes, phi of n will be p-1*q-1
	public static BigInteger phi(BigInteger p, BigInteger q){
		BigInteger one = BigInteger.ONE;
		BigInteger pOne=p.subtract(one);
		BigInteger qOne=q.subtract(one);
		BigInteger phi_of_n= pOne.multiply(qOne);
		return phi_of_n;
		
	}
	
	//Finds the greatest common denominator for two bigintegers
	public static BigInteger gcd(BigInteger a, BigInteger b){
		return a.gcd(b);
	}
	
	public static BigInteger inverse(BigInteger a, BigInteger m){
		return a.modInverse(m);
	}
	
	//converts the contents of a file to text in a string
	public static String fileToString (String fileName){
		String result="";
		try{
			FileInputStream file = new FileInputStream(fileName);
			byte[] b = new byte[file.available()];
			file.read(b);
			file.close();
			result = new String(b);
		}
		catch(Exception e){
			System.out.println("Conversion failed");
		}
		return result;
	}
	
	//Converts the string from a file, to arraylist blocks of size 200
	public static ArrayList<String> toStringBlocks(String s){
		ArrayList<String> sBlocks = new ArrayList<String>();
		int size = s.length()/200+1;
		int start = 0;
		int end = 200;
		for(int i = 0; i<size; i++){
			if(s.length()<end){
				sBlocks.add(s.substring(start, s.length()));
			}else{
				sBlocks.add(s.substring(start, end));
				start = start+200;
				end = end + 200;
				
			}
		}
		return sBlocks;
	}
	
	//Converts string blocks to blocks of BigIntegers, based on the padding scheme
	public static ArrayList<BigInteger> toBigIntBlocks(ArrayList<String> sBlocks){
		ArrayList<BigInteger> Block = new ArrayList<BigInteger>();
		BigInteger num;
		String s;
		for(int i = 0; i < sBlocks.size(); i++){
			char [] chararray = sBlocks.get(i).toCharArray();
			num = BigInteger.ZERO;
			s = "";
			for(int j = 0; j < chararray.length; j++){
				s += toPaddedAscii(chararray[j]);
			}
			num = new BigInteger(s);
			Block.add(num);
		}
		return Block;
	}
	
	//Encrypts the BigInteger blocks
	public static ArrayList<BigInteger> encrypt(ArrayList<BigInteger> plainText, BigInteger e, BigInteger n){
		ArrayList<BigInteger> cipherText = new ArrayList<BigInteger>();
		BigInteger block;
		for (int i = 0; i < plainText.size(); i++) {
			block = plainText.get(i);
			block = block.modPow(e, n);
			cipherText.add(block);
		}
		
		System.out.println("Encrypted");
		return cipherText;
	}
	
	
	//decrypts the encrypted BigInteger blocks
	public static ArrayList<BigInteger> decrypt(ArrayList<BigInteger> cipherText, BigInteger d, BigInteger n){
		ArrayList<BigInteger> decipherText = new ArrayList<BigInteger>();
		BigInteger block;
		for (int i = 0; i < cipherText.size(); i++) {
			block = cipherText.get(i);
			block = block.modPow(d, n);
			decipherText.add(block);
		}
		System.out.println("Decrypted");
		return decipherText;
	}
	
	//unpads BigInteger blocks
	public static ArrayList<String> plainTextToStringBlocks(ArrayList<BigInteger> decipherText){
		ArrayList<String> stringBlock = new ArrayList<String>();
		for(int i = 0; i < decipherText.size(); i++){
			String str = decipherText.get(i).toString();
			String a = "";
			for(int j = 0; j < str.length(); j+=3){
				if(j+3 < str.length()){
					a += ""+unPaddedAscii(str.substring(j,j+3));
				}else{
					a += ""+unPaddedAscii(str.substring(j,str.length()));
				}
			}
			stringBlock.add(a);
		}
		return stringBlock;
	}
	
	//Outputs the string blocks to a file
	public static void stringToFile (ArrayList<String> text){
	String s = "";
		for(int i = 0; i < text.size(); i++){
			s += text.get(i);
		}
		try {
			File file = new File("result.txt");
			FileOutputStream fop = new FileOutputStream(file);
			byte[] contentInBytes = s.getBytes();
			fop.write(contentInBytes);
			fop.flush();
			fop.close();
			System.out.println("Printed out to file");
		}
		catch (IOException e){
		e.printStackTrace();
		}
	}
	
	//The padding scheme is taking each characters ascii value and adding 100 to it
	public static String toPaddedAscii(Character c){
		String s = Integer.toString(c+100);
		return s;
	}
	
	//Returns padded characters to normal
	public static char unPaddedAscii(String c){
		int s = Integer.valueOf(c) - 100;
		return (char) s;
	}
}