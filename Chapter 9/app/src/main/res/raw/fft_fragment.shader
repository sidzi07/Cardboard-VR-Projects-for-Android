precision mediump float;        // default medium precision
uniform sampler2D u_Texture;    // the input texture

varying vec2 v_TexCoordinate;   // interpolated texture coordinate per fragment
uniform vec4 u_Color;

void main() {
    vec4 color;
    if(v_TexCoordinate.y < texture2D(u_Texture,
    v_TexCoordinate).r){
        color = u_Color;
    }
    gl_FragColor = color;
}
