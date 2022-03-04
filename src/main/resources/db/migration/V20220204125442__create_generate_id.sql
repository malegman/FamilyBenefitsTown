CREATE OR REPLACE FUNCTION family_benefit_town.generate_id(len INTEGER) RETURNS TEXT
  AS $$

  DECLARE
chars TEXT[] := '{0,1,2,3,4,5,6,7,8,9,A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z,a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z}';
    result TEXT := '';

BEGIN
    IF len < 0
      THEN RAISE EXCEPTION 'Length can''t be less than 0';
END IF;

FOR i IN 1..len LOOP
      result := result || chars[floor(1 + 62 * random())];
END LOOP;

RETURN result;
END;

  $$
LANGUAGE plpgsql
  STRICT
  SECURITY DEFINER;

REVOKE ALL ON FUNCTION family_benefit_town.generate_id(len INTEGER) FROM PUBLIC;
GRANT EXECUTE ON FUNCTION family_benefit_town.generate_id(len INTEGER) TO familyben;
