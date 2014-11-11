package queryautocomplete;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;


public class QueryAutoComplete 
{
    private int MAX=100009;
    private double increment=0.4;
    private int MaxS;
    private int TrieNodeCounter;
   
    private ArrayList<DigitalCompressedTrie>Trie = new ArrayList<DigitalCompressedTrie>(MAX);
    
    
    /**
     * @param args the command line arguments
     */
    
    
    public QueryAutoComplete(int max) 
    {
        this.MaxS=max;
        DigitalCompressedTrie.MaxS=max;
        
        TrieNodeCounter=0;
        Trie.add(null);
        Trie.set(0, new DigitalCompressedTrie() );
        Trie.get(0).par=0;
        
    }
    
    public void updateString(String query)
    {
        int edgeId; int cur=0,indx=0,match;
        int CloneNode,NewNode,OldNode;
        
        int lastNode=-1,lastNode2=-1;
        
        while( indx<query.length() && lastNode==-1 )
        {
            if( Trie.get(cur).Map.containsKey(  query.charAt(indx) )  )
            {
                IIPair pp=(IIPair)Trie.get(cur).Map.get(query.charAt(indx));
                edgeId=pp.sc;
                String str=Trie.get(cur).EdgeList.get(edgeId);
                
                match=0;
                while(indx<query.length() && match<str.length() &&  query.charAt(indx)==str.charAt(match) ) {++indx;++match;}
                
                if(match>=str.length() && indx<query.length()) cur=pp.fs;  // edge end but query not end.

                if( indx>=query.length() &&  match<str.length() ) // query end but edge not end.
                {
                    Trie.add(null);
                    NewNode=++TrieNodeCounter;
                    OldNode=pp.fs;
                    
                    Trie.set(NewNode, new DigitalCompressedTrie() ); // new node
                    
                    
                    //updating
                    Trie.get(NewNode).updateMap( str.charAt(match) , str.substring(match), OldNode, -1);
                    Trie.get(NewNode).freq++;
                    Trie.get(NewNode).updatePar(cur, edgeId);
                    Trie.get(OldNode).updatePar(NewNode,0);
                    Trie.get(cur).updateMap(str.charAt(0), str.substring(0,match), NewNode , edgeId);
                    lastNode=OldNode;
                }
                
                if(indx>=query.length() && match>=str.length()) //both string ended
                {
                    OldNode=pp.fs;
                    Trie.get(OldNode).freq++;
                    lastNode=OldNode;
                }
                
                if(indx<query.length() && match<str.length()) // no one end
                {
                    // create two node.
                    Trie.add(null);
                    CloneNode=++TrieNodeCounter;
                    Trie.set(CloneNode,new DigitalCompressedTrie());
                    
                    Trie.add(null);
                    NewNode=++TrieNodeCounter;
                    Trie.set(NewNode,new DigitalCompressedTrie());
                    
                    OldNode=pp.fs;
                    
                    //updating
                    Trie.get(cur).updateMap(str.charAt(0) , str.substring(0, match), CloneNode, edgeId);
                    Trie.get(CloneNode).updateMap( str.charAt(match) , str.substring(match), OldNode, -1);
                    Trie.get(CloneNode).updateMap( query.charAt(indx) , query.substring(indx), NewNode, -1);
                    Trie.get(CloneNode).updatePar(cur, edgeId);
                    Trie.get(OldNode).updatePar(CloneNode, 0);
                    Trie.get(NewNode).updatePar(CloneNode, 1);
                    Trie.get(NewNode).freq++;
                    lastNode=OldNode;
                    lastNode2=NewNode;
                }
                
            }
			
            else 
            {
                // new node
                Trie.add(null);
                NewNode=++TrieNodeCounter;
                Trie.set(NewNode, new DigitalCompressedTrie() );
                
                //updating
                Trie.get(cur).updateMap(query.charAt(indx), query.substring(indx), NewNode , -1);
                HashMap hm= Trie.get(cur).Map;
                
                Object pp1=hm.get(query.charAt(indx) );
                IIPair pp=(IIPair) pp1;
                
                Trie.get(NewNode).updatePar( cur , pp.sc  );
                Trie.get(NewNode).freq++;
                
                lastNode=NewNode;
                break;
            }
        }
        
        if(lastNode!=-1) updateBestOptions(lastNode);
        if(lastNode2!=-1) updateBestOptions(lastNode2);
    }
    
    
    private void updateBestOptions(int nowNode)
    {
        DIPair pp= new DIPair(Trie.get(nowNode).freq , nowNode);  // Pair:-- (freq,node).
        Trie.get(nowNode).updateBestNode( pp );
        
        ArrayList<DIPair>BestOptions;
        BestOptions=Trie.get(nowNode).BestPairs;
        
        while(nowNode!=0)
        {
            nowNode=Trie.get(nowNode).par;
            for(int i=0;i<BestOptions.size();i++) Trie.get(nowNode).updateBestNode(BestOptions.get(i));
            
            BestOptions=Trie.get(nowNode).BestPairs;
        }
        
    }
    
    
    private void getBestPairs( ArrayList<DIPair>BestNodes, DIPair  node)
    {
        if( node.fs>0.0 ) 
        {
            int curIndx;
            int Extra=2;
            for(curIndx=0;curIndx<BestNodes.size();curIndx++)
            {
                if(BestNodes.get(curIndx).sc == node.sc) 
                {
                    BestNodes.get(curIndx).fs=Math.max(BestNodes.get(curIndx).fs, node.fs);
                    break;
                }
            }
            if(curIndx>=BestNodes.size()) BestNodes.add(node);

            for(int i=BestNodes.size()-1;i-1>=0;i--)
            {
                if( BestNodes.get(i).fs>BestNodes.get(i-1).fs ) Collections.swap(BestNodes, i, i-1);
            }
            if(BestNodes.size()>MaxS + Extra ) BestNodes.remove(BestNodes.size()-1);
        }
    }
    
    public ArrayList<String> getSuggestion(String query)
    {
        int curNode=0,indx=0;
        double multiplier=0.0;
        ArrayList<DIPair>BestPairs= new ArrayList<DIPair>(); // Pair:-->> (freq,node).
        
        while(true)
        {
            for(int i=0;i<Trie.get(curNode).BestPairs.size();i++)
            {
                DIPair pp= new DIPair(Trie.get(curNode).BestPairs.get(i).fs*multiplier, Trie.get(curNode).BestPairs.get(i).sc);
                getBestPairs(BestPairs,pp);
            }
            if(indx>=query.length()) break;
            
            if(Trie.get(curNode).Map.containsKey(query.charAt(indx)))
            {
                int match=0;
                IIPair pp=(IIPair)Trie.get(curNode).Map.get(query.charAt(indx));
                String str=Trie.get(curNode).EdgeList.get(pp.sc);
                
                while(match<str.length() && indx<query.length() && str.charAt(match)==query.charAt(indx)) {match++;indx++;}
                
                curNode=pp.fs;
                
                if(match<str.length()) indx=query.length();
                if(indx>=query.length() && Trie.get(curNode).freq>0.0 ) // perfect match or query ended.
                {
                    getBestPairs(BestPairs, new DIPair(9999999.0, curNode) );
                }
                
                if(indx>=query.length() ) // perfect match or query ended & may not be the leaf node.
                {
                    DIPair pp2;
                    for(int i=0;i<Trie.get(curNode).BestPairs.size();i++)
                    {
                        pp2= new DIPair(100000.0 + Trie.get(curNode).BestPairs.get(i).fs*multiplier, Trie.get(curNode).BestPairs.get(i).sc);
                        getBestPairs(BestPairs,pp2);
                    }
                }
                
            }
			
            else if(curNode!=0)  /// query didn't end but this is the end node 
            {
                if(Trie.get(curNode).freq>0.0) getBestPairs(BestPairs, new DIPair(9999996.0, curNode) );  // leaf node.
                
                DIPair pp2;
                for(int i=0;i<Trie.get(curNode).BestPairs.size();i++)
                {
                    pp2= new DIPair(100000.0 + Trie.get(curNode).BestPairs.get(i).fs*multiplier, Trie.get(curNode).BestPairs.get(i).sc);
                    getBestPairs(BestPairs,pp2);
                }

                break;
            }
            else break;
            multiplier=multiplier+increment;
        }
        
        ArrayList<String>BestSuggestion= new ArrayList<String>();
        
        int par,parEdge;
        for(int i=0;i<BestPairs.size() && i<MaxS;i++)
        {
            curNode=BestPairs.get(i).sc;
            String str="";
            while(curNode!=0)
            {
                par=Trie.get(curNode).par;
                parEdge=Trie.get(curNode).parEdge;
                
                str=str+ new StringBuffer( Trie.get(par).EdgeList.get(parEdge) ).reverse().toString();
                curNode=par;
            }
            String reverse = new StringBuffer(str).reverse().toString();
            BestSuggestion.add(reverse);
        }
        
        return BestSuggestion;
    }
}
