package test;

class hi
{
	String h;
}
class Y
{
	hi t[];
	Y()
	{
		t=new hi [5];
	}
	public static void main(String[] args)
	{
		Y n=new Y();
		n.t[0].h="hello";
		System.out.println(n.t[0].h);
	}
}