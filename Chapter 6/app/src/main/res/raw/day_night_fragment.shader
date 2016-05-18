precision highp float;
//  default high precision for floating point ranges of the
//  planets
uniform vec3 u_LightPos;      // light position in eye space
uniform vec4 u_LightCol;
uniform sampler2D u_Texture;  // the day texture.
uniform sampler2D u_NightTexture;    // the night texture.

varying vec3 v_Position;
varying vec3 v_Normal;
varying vec2 v_TexCoordinate;

void main() {
    // lighting direction vector from the light to the vertex
    vec3 lightVector = normalize(u_LightPos - v_Position);

    // dot product of the light vector and vertex normal. If the
    // normal and light vector are
    // pointing in the same direction then it will get max
    // illumination.
    float ambient = 0.3;
    float dotProd = dot(v_Normal, lightVector);
    float blend = min(1.0, dotProd * 2.0);
    if(dotProd < 0.0){
        //flat ambient level of 0.3
        gl_FragColor = texture2D(u_NightTexture, v_TexCoordinate) * ambient;
    } else {
        gl_FragColor = ( texture2D(u_Texture, v_TexCoordinate) * blend
            + texture2D(u_NightTexture, v_TexCoordinate) * (1.0 - blend)
        ) * u_LightCol * min(max(dotProd * 2.0, ambient), 1.0);
    }
}
