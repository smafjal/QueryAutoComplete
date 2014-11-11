package queryautocomplete;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;


public class DigitalCompressedTrie 
{
    protected static int MaxS;
    int par,parEdge;
    double freq;
    
    HashMap Map = new HashMap(); // Pair:-->> (Node,EdgeIdIndex)  
    
    ArrayList<DIPair>BestPairs = new ArrayList<DIPair>();//Pair:-->>  (Freq,Node)
    ArrayList<String>EdgeList = new ArrayList<String>();
    
    public DigitalCompressedTrie() 
    {
        par=-1; parEdge=-1; freq=0.0;
        Map.clear();
        EdgeList.clear();
        BestPairs.clear();
    }
    
    public void updateMap(char ch,String str , int nodeIdx , int edgeIdx )
    {        
        if(edgeIdx==-1)
        {
            Map.put(ch, new IIPair(nodeIdx,  EdgeList.size() ) );  // Pair:--->> (nodeIdx,edgeIdx)
            EdgeList.add(str);
        }
        else 
        {
            Map.put(ch, new IIPair(nodeIdx,  edgeIdx ) );
            EdgeList.set(edgeIdx, str);  
        }
    }
    
    public void updatePar(int parNode , int ParNodeEdge)
    {
        this.par=parNode;
        this.parEdge=ParNodeEdge;
    }
    
    public void updateBestNode( DIPair  node)
    {
        boolean flag=true;
        for(int curIndx=0;curIndx<BestPairs.size();curIndx++)
        {
            if(BestPairs.get(curIndx).sc == node.sc) 
            {
                BestPairs.get(curIndx).fs=node.fs;
                flag=false;
                break;
            }
        }
        if(flag) 
        {
            BestPairs.add(node);
        }
        
        for(int i=BestPairs.size()-1;i>0;i--)
        {
            if( BestPairs.get(i).fs>BestPairs.get(i-1).fs ) 
            {
                Collections.swap(BestPairs, i, i-1);
            }
        }
        
        int sz=BestPairs.size();
        if(sz>MaxS) BestPairs.remove(sz-1);
    }
    
}
